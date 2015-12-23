/*
 * Copyright 2005-2015 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * The use of the Apache License does not indicate that this project is
 * affiliated with the Apache Software Foundation.
 */
package com.marklogic.developer.corb;

import static com.marklogic.developer.corb.TestUtils.clearSystemProperties;
import static com.marklogic.developer.corb.TestUtils.createTempDirectory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.developer.corb.util.FileUtils;

/**
 *
 * @author Mads Hansen, MarkLogic Corporation
 */
public class PostBatchUpdateFileTaskTest {

    private String bottomContent = "col1,col2,col3,col4";
    private String bakExt = ".bak";
    private String partExt = ".part";
    private String exampleContent = "The quick brown fox jumped over the lazy dog.";

    public PostBatchUpdateFileTaskTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        clearSystemProperties();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getBottomContent method, of class PostBatchUpdateFileTask.
     */
    @Test
    public void testGetBottomContent() {
        System.out.println("getBottomContent");
        PostBatchUpdateFileTask instance = new PostBatchUpdateFileTask();
        instance.properties = new Properties();
        instance.properties.setProperty("EXPORT-FILE-BOTTOM-CONTENT", bottomContent);
        String result = instance.getBottomContent();
        assertEquals(bottomContent, result);
    }

    @Test
    public void testGetBottomContent_nullBottom() {
        System.out.println("getBottomContent");
        PostBatchUpdateFileTask instance = new PostBatchUpdateFileTask();
        String result = instance.getBottomContent();
        assertNull(result);
    }

    /**
     * Test of writeBottomContent method, of class PostBatchUpdateFileTask.
     */
    @Test
    public void testWriteBottomContent() throws Exception {
        System.out.println("writeBottomContent");
        String expextedResult = bottomContent.concat("\n");
        String filename = "export.csv";
        PostBatchUpdateFileTask instance = new PostBatchUpdateFileTask();
        instance.properties = new Properties();
        instance.properties.setProperty("EXPORT-FILE-BOTTOM-CONTENT", bottomContent);
        instance.properties.setProperty("EXPORT-FILE-NAME", filename);
        File tempDir = createTempDirectory();
        instance.exportDir = tempDir.toString();

        instance.writeBottomContent();

        File outputFile = new File(tempDir, instance.getPartFileName());
        String outputText = TestUtils.readFile(outputFile);
        assertEquals(expextedResult, outputText);
    }

    /**
     * Test of moveFile method, of class PostBatchUpdateFileTask.
     */
    @Test
    public void testMoveFile_String_String() throws Exception {
        System.out.println("moveFile");
        File source = createSampleFile();
        String destFilePath = source.toString() + bakExt;
        PostBatchUpdateFileTask instance = new PostBatchUpdateFileTask();
        instance.moveFile(source.toString(), destFilePath);

        File dest = new File(destFilePath);
        assertFalse(source.exists());
        assertTrue(dest.exists());
        assertEquals(exampleContent, TestUtils.readFile(dest));
    }

    @Test
    public void testMoveFile_String_String_destExists() throws Exception {
        System.out.println("moveFile");
        File source = createSampleFile();
        String destFilePath = source.toString() + bakExt;
        File dest = new File(destFilePath);
        dest.deleteOnExit();
        dest.createNewFile();
        PostBatchUpdateFileTask instance = new PostBatchUpdateFileTask();
        instance.moveFile(source.toString(), destFilePath);

        assertFalse(source.exists());
        assertTrue(dest.exists());
        assertEquals(exampleContent, TestUtils.readFile(dest));
    }

    /**
     * Test of moveFile method, of class PostBatchUpdateFileTask.
     */
    @Test
    public void testMoveFile_0args() throws Exception {
        System.out.println("moveFile");
        File partFile = createSamplePartFile();
        String partFilePath = partFile.toString();
        String exportFilePath = partFilePath.substring(0, partFilePath.lastIndexOf("."));
        Properties props = new Properties();
        props.setProperty("EXPORT-FILE-PART-EXT", partExt);
        props.setProperty("EXPORT-FILE-NAME", exportFilePath);
        PostBatchUpdateFileTask instance = new PostBatchUpdateFileTask();
        instance.properties = props;
        instance.moveFile();

        File dest = new File(exportFilePath);
        assertTrue(dest.exists());
        assertFalse(partFile.exists());
        assertEquals(exampleContent, TestUtils.readFile(dest));
    }

    /**
     * Test of compressFile method, of class PostBatchUpdateFileTask.
     */
    @Test
    public void testCompressFile() throws Exception {
        System.out.println("compressFile");
        File sampleFile = createSamplePartFile();
        Properties props = new Properties();
        props.setProperty("EXPORT_FILE_AS_ZIP", "true");
        props.setProperty("EXPORT-FILE-NAME", sampleFile.toString());
        props.setProperty("EXPORT-FILE-PART-EXT", ".zpart");

        PostBatchUpdateFileTask instance = new PostBatchUpdateFileTask();
        instance.properties = props;
        instance.compressFile();

        File output = new File(sampleFile.toString().concat(".zip"));
        output.deleteOnExit();
        assertTrue(output.exists());
    }

    @Test
    public void testCompressFile_outputFileDoesNotExist() throws Exception {
        System.out.println("compressFile");
        File sampleFile = createSamplePartFile();
        sampleFile.delete();
        Properties props = new Properties();
        props.setProperty("EXPORT_FILE_AS_ZIP", "true");
        props.setProperty("EXPORT-FILE-NAME", sampleFile.toString());
        props.setProperty("EXPORT-FILE-PART-EXT", ".zpart");

        PostBatchUpdateFileTask instance = new PostBatchUpdateFileTask();
        instance.properties = props;
        instance.compressFile();

        File output = new File(sampleFile.toString().concat(".zip"));
        output.deleteOnExit();
        assertFalse(output.exists());
    }

    @Test
    public void testCompressFile_zipPartExists() throws Exception {
        System.out.println("compressFile");
        File sampleFile = createSamplePartFile();
        Properties props = new Properties();
        props.setProperty("EXPORT_FILE_AS_ZIP", "true");
        props.setProperty("EXPORT-FILE-NAME", sampleFile.toString());
        props.setProperty("EXPORT-FILE-PART-EXT", "zpart");
        String zipFilePart = sampleFile.toString().concat(".zip.zpart");
        File existingZipFilePart = new File(zipFilePart);
        existingZipFilePart.createNewFile();

        PostBatchUpdateFileTask instance = new PostBatchUpdateFileTask();
        instance.properties = props;
        instance.compressFile();

        File output = new File(sampleFile.toString().concat(".zip"));
        output.deleteOnExit();
        assertTrue(output.exists());
    }

    /**
     * Test of call method, of class PostBatchUpdateFileTask.
     */
    @Test
    public void testCall_removeDuplicatesAndSort_ascUniq() throws Exception {
        System.out.println("call");
        Properties props = new Properties();
        props.setProperty("EXPORT-FILE-SORT", "asc|uniq");
        String result = testRemoveDuplicatesAndSort(props);
        List<String> tokens = Arrays.asList(result.split("\n"));
        assertEquals(4, tokens.size());
        for (String next : new String[]{"a", "b", "d", "z"}) {
            assertTrue(tokens.contains(next));
        }
    }

    @Test
    public void testCall_removeDuplicatesAndSort_ASCENDING() throws Exception {
        System.out.println("call");
        Properties props = new Properties();
        props.setProperty("EXPORT-FILE-SORT", "ASCENDING|distinct");
        String result = testRemoveDuplicatesAndSort(props);
        assertEquals(splitAndAppendNewline("a,b,d,z"), result);
    }

    @Test
    public void testCall_removeDuplicatesAndSort_trueSort_withHeaderAndFooter() throws Exception {
        System.out.println("call");
        String header = "BEGIN\nletter\n";
        File file = File.createTempFile("moveFile", "txt");
        file.deleteOnExit();
        FileWriter writer = new FileWriter(file, true);
        writer.append(header);
        writer.append("z\n");
        writer.append("d\n");
        writer.append("d\n");
        writer.append("a\n");
        writer.append("b\n");
        writer.flush();
        writer.close();

        Properties props = new Properties();
        props.setProperty("EXPORT-FILE-TOP-CONTENT", header);
        props.setProperty("EXPORT-FILE-HEADER-LINE-COUNT", "2");
        props.setProperty("EXPORT-FILE-SORT", "ascending|distinct");
        props.setProperty("EXPORT-FILE-BOTTOM-CONTENT", "END");
        String result = testRemoveDuplicatesAndSort(file, props);
        assertEquals(splitAndAppendNewline("BEGIN,letter,a,b,d,z,END"), result);
    }

    @Test
    public void testCall_removeDuplicatesAndSort_customComparator() throws Exception {
       testCustomComparator(null, "b,z...,d....,d....,a.....");
    }
    
    @Test
    public void testCall_removeDuplicatesAndSort_customComparator_distinct() throws Exception {
        testCustomComparator("distinct", "b,z...,d....,a.....");
    }
    
    @Test
    public void testCall_removeDuplicatesAndSort_customComparator_badClass() throws Exception {
        testCustomComparator("distinct", "z...,d....,d....,a.....,b", "java.lang.String");
    }
    
    @Test 
    public void testCall_removeDuplicatesAndSort_noSortOrDedup_distinctOnly() throws Exception {
        testCustomComparator("distinct", "z...,d....,d....,a.....,b", null);
    }
    
    @Test 
    public void testCall_removeDuplicatesAndSort_noSortOrDedup_blankSort() throws Exception {
        testCustomComparator(" ", "z...,d....,d....,a.....,b", null);
    }
    
    public void testCustomComparator(String sortProperty, String expected) throws Exception {
        testCustomComparator(sortProperty, expected, "com.marklogic.developer.corb.PostBatchUpdateFileTaskTest$StringLengthComparator");
    }
    
    public void testCustomComparator(String sortProperty, String expected, String comparator) throws Exception {
        System.out.println("call");
        File file = File.createTempFile("moveFile", "txt");
        file.deleteOnExit();
        FileWriter writer = new FileWriter(file, true);
        writer.append("z...\n");
        writer.append("d....\n");
        writer.append("d....\n");
        writer.append("a.....\n");
        writer.append("b\n");
        writer.flush();
        writer.close();

        Properties props = new Properties();
        if (comparator != null) {
            props.setProperty("EXPORT-FILE-SORT-COMPARATOR", comparator);
        }
        if (sortProperty != null) {
            props.setProperty("EXPORT-FILE-SORT", sortProperty);
        }
        String result = testRemoveDuplicatesAndSort(file, props);
        assertEquals(splitAndAppendNewline(expected), result);
    }
    
    public static class StringLengthComparator implements Comparator<String> {

        @Override
        public int compare(String o1, String o2) {
            if (o1.length() > o2.length()) {
                return 1;
            } else if (o1.length() < o2.length()) {
                return -1;
            } else {
                return 0;
            }
        }
    }
    
    @Test
    public void testCall_removeDuplicatesAndSort_descendingDistinct() throws Exception {
        System.out.println("call");
        Properties props = new Properties();
        props.setProperty("EXPORT-FILE-SORT", "desc|distinct");
        String result = testRemoveDuplicatesAndSort(props);
        assertEquals(splitAndAppendNewline("z,d,b,a"), result);
    }

    @Test
    public void testCall_removeDuplicatesAndSort_invalidValue() throws Exception {
        System.out.println("call");
        Properties props = new Properties();
        props.setProperty("EXPORT-FILE-SORT", "false");
        String result = testRemoveDuplicatesAndSort(props);
        assertEquals(splitAndAppendNewline("z,d,d,a,b"), result);
    }

    private String splitAndAppendNewline(String values) {
        StringBuilder sb = new StringBuilder();

        for (String value : values.split(",")) {
            sb.append(value)
                    .append("\n");
        }
        return sb.toString();
    }

    @Test
    public void testCall_removeDuplicatesAndSort_emptyFile_extensionWithoutDot() throws Exception {
        File file = File.createTempFile("moveFile", "txt");
        file.deleteOnExit();
        file.createNewFile();
        Properties props = new Properties();
        props.setProperty("EXPORT-FILE-SORT", "true");
        props.setProperty("EXPORT-FILE-NAME", file.toString());
        props.setProperty("EXPORT-FILE-PART-EXT", "pt");
        PostBatchUpdateFileTask instance = new PostBatchUpdateFileTask();
        instance.properties = props;
        String[] result = instance.call();
        assertTrue(file.length() == 0);
    }

    @Test
    public void testCall_removeDuplicatesAndSort_emptyFile_emptyExtension() throws Exception {
        File file = File.createTempFile("moveFile", "txt");
        file.deleteOnExit();
        file.createNewFile();
        Properties props = new Properties();
        props.setProperty("EXPORT-FILE-SORT", "true");
        props.setProperty("EXPORT-FILE-NAME", file.toString());
        props.setProperty("EXPORT-FILE-PART-EXT", "");
        PostBatchUpdateFileTask instance = new PostBatchUpdateFileTask();
        instance.properties = props;
        String[] result = instance.call();
        assertTrue(file.length() == 0);
    }

    @Test
    public void testMoveFile() throws IOException {
        File file = File.createTempFile("moveFile", "txt");
        file.deleteOnExit();
        file.createNewFile();
        FileUtils.moveFile(file, file);
    }


    private String testRemoveDuplicatesAndSort(Properties props) throws IOException, Exception {
        File file = File.createTempFile("moveFile", "txt");
        file.deleteOnExit();
        FileWriter writer = new FileWriter(file, true);
        writer.append("z\n");
        writer.append("d\n");
        writer.append("d\n");
        writer.append("a\n");
        writer.append("b\n");
        writer.close();
        return testRemoveDuplicatesAndSort(file, props);
    }

    private String testRemoveDuplicatesAndSort(File fileToSort, Properties props) throws IOException, Exception {
        System.out.println("call");

        props.setProperty("EXPORT_FILE_AS_ZIP", "false");
        props.setProperty("EXPORT-FILE-NAME", fileToSort.toString());

        PostBatchUpdateFileTask instance = new PostBatchUpdateFileTask();
        instance.properties = props;
        String[] result = instance.call();
        File output = new File(fileToSort.toString());
        output.deleteOnExit();
        return TestUtils.readFile(output);
    }

    @Test(expected = NullPointerException.class)
    public void testCall() throws Exception {
        System.out.println("call");

        PostBatchUpdateFileTask instance = new PostBatchUpdateFileTask();
        String[] result = instance.call();
    }

    private File createSampleFile() throws IOException {
        return createSampleFile(".tmp");
    }

    private File createSampleFile(String extension) throws IOException {
        File file = File.createTempFile("moveFile", extension);
        file.deleteOnExit();
        FileWriter writer = new FileWriter(file, true);
        writer.append(exampleContent);
        writer.close();
        return file;
    }

    private File createSamplePartFile() throws IOException {
        return createSampleFile(".part");
    }
}
