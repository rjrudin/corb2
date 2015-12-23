/*
 * * Copyright 2005-2015 MarkLogic Corporation
 * *
 * * Licensed under the Apache License, Version 2.0 (the "License");
 * * you may not use this file except in compliance with the License.
 * * You may obtain a copy of the License at
 * *
 * * http://www.apache.org/licenses/LICENSE-2.0
 * *
 * * Unless required by applicable law or agreed to in writing, software
 * * distributed under the License is distributed on an "AS IS" BASIS,
 * * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * * See the License for the specific language governing permissions and
 * * limitations under the License.
 * *
 * * The use of the Apache License does not indicate that this project is
 * * affiliated with the Apache Software Foundation.
 */
package com.marklogic.developer.corb;

import static com.marklogic.developer.corb.TestUtils.clearSystemProperties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.developer.TestHandler;
import com.marklogic.xcc.ContentSource;
import com.marklogic.xcc.ModuleInvoke;
import com.marklogic.xcc.Request;
import com.marklogic.xcc.ResultSequence;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.exceptions.QueryException;
import com.marklogic.xcc.exceptions.QueryStackFrame;
import com.marklogic.xcc.exceptions.RequestException;
import com.marklogic.xcc.exceptions.RequestPermissionException;
import com.marklogic.xcc.exceptions.RequestServerException;
import com.marklogic.xcc.exceptions.RetryableJavaScriptException;
import com.marklogic.xcc.exceptions.RetryableQueryException;
import com.marklogic.xcc.exceptions.RetryableXQueryException;
import com.marklogic.xcc.exceptions.ServerConnectionException;
import com.marklogic.xcc.exceptions.XQueryException;
import com.marklogic.xcc.types.XdmBinary;
import com.marklogic.xcc.types.XdmItem;

/**
 *
 * @author Mads Hansen, MarkLogic Corporation
 */
public class AbstractTaskTest {

    private final TestHandler testLogger = new TestHandler();

    public AbstractTaskTest() {
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
        Logger logger = Logger.getLogger(AbstractTask.class.getSimpleName());
        logger.addHandler(testLogger);
    }

    @After
    public void tearDown() {
        clearSystemProperties();
    }

    /**
     * Test of setContentSource method, of class AbstractTask.
     */
    @Test
    public void testSetContentSource() {
        System.out.println("setContentSource");
        ContentSource cs = mock(ContentSource.class);
        AbstractTask instance = new AbstractTaskImpl();
        instance.setContentSource(cs);
        assertEquals(cs, instance.cs);
    }

    /**
     * Test of setModuleType method, of class AbstractTask.
     */
    @Test
    public void testSetModuleType() {
        System.out.println("setModuleType");
        String moduleType = "foo";
        AbstractTask instance = new AbstractTaskImpl();
        instance.setModuleType(moduleType);
        assertEquals(moduleType, instance.moduleType);
    }

    /**
     * Test of setModuleURI method, of class AbstractTask.
     */
    @Test
    public void testSetModuleURI() {
        System.out.println("setModuleURI");
        String moduleUri = "test.xqy";
        AbstractTask instance = new AbstractTaskImpl();
        instance.setModuleURI(moduleUri);
        assertEquals(moduleUri, instance.moduleUri);
    }

    /**
     * Test of setAdhocQuery method, of class AbstractTask.
     */
    @Test
    public void testSetAdhocQuery() {
        System.out.println("setAdhocQuery");
        String adhocQuery = "adhoc.xqy";
        AbstractTask instance = new AbstractTaskImpl();
        instance.setAdhocQuery(adhocQuery);
        assertEquals(adhocQuery, instance.adhocQuery);
    }

    /**
     * Test of setQueryLanguage method, of class AbstractTask.
     */
    @Test
    public void testSetQueryLanguage() {
        System.out.println("setQueryLanguage");
        String language = "XQuery";
        AbstractTask instance = new AbstractTaskImpl();
        instance.setQueryLanguage(language);
        assertEquals(language, instance.language);
    }

    /**
     * Test of setProperties method, of class AbstractTask.
     */
    @Test
    public void testSetProperties() {
        System.out.println("setProperties");
        Properties props = new Properties();
        Properties properties = props;
        AbstractTask instance = new AbstractTaskImpl();
        instance.setProperties(properties);
        assertEquals(properties, instance.properties);
    }

    /**
     * Test of setInputURI method, of class AbstractTask.
     */
    @Test
    public void testSetInputURI() {
        System.out.println("setInputURI");
        String[] inputUri = {"foo", "bar", "baz"};
        AbstractTask instance = new AbstractTaskImpl();
        instance.setInputURI(inputUri);
        Assert.assertArrayEquals(inputUri, instance.inputUris);
    }

    @Test
    public void testSetInputURI_null() {
        System.out.println("setInputURI");
        AbstractTask instance = new AbstractTaskImpl();
        assertNull(instance.inputUris);
        instance.setInputURI(null);
        assertNotNull(instance.inputUris);
    }

    /**
     * Test of setFailOnError method, of class AbstractTask.
     */
    @Test
    public void testSetFailOnError() {
        System.out.println("setFailOnError");
        AbstractTask instance = new AbstractTaskImpl();
        instance.setFailOnError(false);
        assertFalse(instance.failOnError);
        instance.setFailOnError(true);
        assertTrue(instance.failOnError);
    }

    /**
     * Test of setExportDir method, of class AbstractTask.
     */
    @Test
    public void testSetExportDir() {
        System.out.println("setExportDir");
        String exportFileDir = "/tmp";
        AbstractTask instance = new AbstractTaskImpl();
        instance.setExportDir(exportFileDir);
        assertEquals(exportFileDir, instance.exportDir);
    }

    /**
     * Test of getExportDir method, of class AbstractTask.
     */
    @Test
    public void testGetExportDir() {
        System.out.println("getExportDir");
        AbstractTask instance = new AbstractTaskImpl();
        String expResult = "/tmp";
        instance.exportDir = expResult;
        String result = instance.getExportDir();
        assertEquals(expResult, result);
    }

    /**
     * Test of newSession method, of class AbstractTask.
     */
    @Test
    public void testNewSession() {
        System.out.println("newSession");
        AbstractTask instance = new AbstractTaskImpl();
        ContentSource cs = mock(ContentSource.class);
        Session session = mock(Session.class);
        when(cs.newSession()).thenReturn(session);
        Session expResult = session;
        instance.cs = cs;
        Session result = instance.newSession();
        assertEquals(expResult, result);
    }

    /**
     * Test of invokeModule method, of class AbstractTask.
     */
    @Test
    public void testInvokeModule() throws Exception {
        System.out.println("invokeModule");
        AbstractTask instance = new AbstractTaskImpl();
        instance.moduleUri = "module.xqy";
        instance.adhocQuery = "adhoc.xqy";
        instance.inputUris = new String[]{"foo", "bar", "baz"};
        ContentSource cs = mock(ContentSource.class);
        Session session = mock(Session.class);
        ModuleInvoke request = mock(ModuleInvoke.class);
        ResultSequence seq = mock(ResultSequence.class);

        when(cs.newSession()).thenReturn(session);
        when(session.newModuleInvoke(anyString())).thenReturn(request);
        when(request.setNewStringVariable(anyString(), anyString())).thenReturn(null);
        when(session.submitRequest(request)).thenReturn(seq);

        instance.cs = cs;
        instance.moduleType = "foo";
        Properties props = new Properties();
        props.setProperty("foo.bar", "baz");
        props.setProperty("foo.baz", "boo");
        props.setProperty("BATCH-URI-DELIM", "");
        instance.properties = props;

        instance.inputUris = new String[]{"uri1", "uri2"};
        instance.invokeModule();
        assertTrue(AbstractTask.MODULE_PROPS.get("foo").contains("foo.bar"));
        assertTrue(AbstractTask.MODULE_PROPS.get("foo").contains("foo.baz"));
    }

    @Test
    public void testGetIntProperty() {
        System.out.println("getIntProperty");
        Properties props = new Properties();
        props.setProperty("one", "one");
        props.setProperty("two", "2");
        props.setProperty("three", "");
        AbstractTask instance = new AbstractTaskImpl();
        instance.properties = props;
        assertEquals(-1, instance.getIntProperty("one"));
        assertEquals(2, instance.getIntProperty("two"));
        assertEquals(-1, instance.getIntProperty("three"));
        assertEquals(-1, instance.getIntProperty("four"));
    }

    @Test
    public void testInvokeModule_RetryableXQueryException() throws Exception {
        Request req = mock(Request.class);
        RetryableXQueryException retryableException = new RetryableXQueryException(req, "code", "401", "1.0-ml", "something bad happened", "", "", true, new String[0], new QueryStackFrame[0]);
        testHandleRequestException("RetryableXQueryException", retryableException, false, 2);
    }

    @Test
    public void testInvokeModule_XQueryException() throws Exception {
        Request req = mock(Request.class);
        XQueryException xqueryException = new XQueryException(req, "code", "401", "1.0-ml", "something bad happened", "", "", true, new String[0], new QueryStackFrame[0]);
        testHandleRequestException("XQueryException", xqueryException, false, 2);
    }

    @Test
    public void testInvokeModule_RetryableJavaScriptException() throws Exception {
        Request req = mock(Request.class);
        RetryableJavaScriptException retryableException = new RetryableJavaScriptException(req, "code", "401", "something bad happened", "", "", true, new String[0], new QueryStackFrame[0]);
        testHandleRequestException("RetryableJavaScriptException", retryableException, false, 2);
    }

    @Test
    public void testHandleRequestException_RequestServerException() throws CorbException, IOException {
        Request req = mock(Request.class);
        RequestServerException serverException = new RequestServerException("something bad happened", req);
        testHandleRequestException("RequestServerException", serverException, false, 2);
    }

    @Test(expected = CorbException.class)
    public void testHandleRequestException_RequestServerException_fail() throws CorbException, IOException {
        Request req = mock(Request.class);
        RequestServerException serverException = new RequestServerException("something bad happened", req);
        testHandleRequestException("RequestServerException", serverException, true, 2);
    }

    @Test
    public void testHandleRequestException_RequestPermissionException() throws CorbException, IOException {
        Request req = mock(Request.class);
        RequestPermissionException serverException = new RequestPermissionException("something bad happened", req, "admin");
        testHandleRequestException("RequestPermissionException", serverException, false, 2);
    }

    @Test(expected = CorbException.class)
    public void testHandleRequestException_RequestPermissionException_fail() throws CorbException, IOException {
        Request req = mock(Request.class);
        RequestPermissionException serverException = new RequestPermissionException("something bad happened", req, "admin");
        testHandleRequestException("RequestPermissionException", serverException, true, 2);
    }

    @Test
    public void testHandleRequestException_ServerConnectionException() throws CorbException, IOException {
        Request req = mock(Request.class);
        ServerConnectionException serverException = new ServerConnectionException("something bad happened", req);
        testHandleRequestException("ServerConnectionException", serverException, false, 2);
    }

    @Test(expected = CorbException.class)
    public void testHandleRequestException_ServerConnectionException_fail() throws CorbException, IOException {
        Request req = mock(Request.class);
        ServerConnectionException serverException = new ServerConnectionException("something bad happened", req);
        testHandleRequestException("ServerConnectionException", serverException, true, 0);
    }

    public void testHandleRequestException(String type, RequestException exception, boolean fail, int retryLimit) throws CorbException, IOException {
        String[] uris = new String[]{"uri1"};
        testHandleRequestException(type, exception, fail, uris, retryLimit);
    }

    public void testHandleRequestException(String type, RequestException exception, boolean fail, String[] uris, int retryLimit) throws CorbException, IOException {
        File exportDir = TestUtils.createTempDirectory();
        File exportFile = File.createTempFile("error", ".err", exportDir);
        testHandleRequestException(type, exception, fail, uris, null, exportDir, exportFile.getName(), retryLimit);
    }

    public void testHandleRequestException(String type, RequestException exception, boolean fail, String[] uris, String delim, File exportDir, String errorFilename, int retryLimit) throws CorbException, IOException {
        System.out.println("handleRequestException");
        if (exportDir == null) {
            exportDir = TestUtils.createTempDirectory();
        }
        AbstractTask instance = new AbstractTaskImpl();
        instance.failOnError = fail;
        instance.inputUris = uris;
        instance.exportDir = exportDir.getAbsolutePath();
        instance.properties = new Properties();
        if (errorFilename != null) {
            instance.properties.setProperty("ERROR-FILE-NAME", errorFilename);
        }
        if (delim != null) {
            instance.properties.setProperty("BATCH-URI-DELIM", delim);
        }

        instance.properties.setProperty("XCC-CONNECTION-RETRY-INTERVAL", "1");
        instance.properties.setProperty("XCC-CONNECTION-RETRY-LIMIT", "" + retryLimit);

        instance.properties.setProperty("QUERY-RETRY-INTERVAL", "1");
        instance.properties.setProperty("QUERY-RETRY-LIMIT", "" + retryLimit);

        instance.handleRequestException(exception);
        List<LogRecord> records = testLogger.getLogRecords();
        assertEquals(Level.WARNING, records.get(0).getLevel());
        if ((exception instanceof RequestServerException
                && !(exception instanceof RetryableQueryException)
                && !(exception instanceof QueryException && ((QueryException) exception).isRetryable()))
                || exception instanceof RequestPermissionException) {
            assertEquals("failOnError is false. Encountered " + type + " at URI: " + instance.asString(uris), records.get(0).getMessage());

        } else if (exception instanceof ServerConnectionException) {
            System.err.println(records.get(0).getMessage());
            assertTrue(records.get(0).getMessage().startsWith("Encountered " + type + " from Marklogic Server. Retrying attempt"));
        }
    }

    public File testWriteToError(String[] uris, String delim, File exportDir, String errorFilename, String message) throws CorbException, IOException {
        Request req = mock(Request.class);
        RequestServerException serverException = new RequestServerException(message, req);
        testHandleRequestException("RequestServerException", serverException, false, uris, delim, exportDir, errorFilename, 1);
        File file = null;
        try {
            file = new File(exportDir, errorFilename);
        } catch (Exception e) {
        }

        return file;
    }

    @Test
    public void testWriteToErrorFile_nullUris() throws CorbException, IOException {
        String[] uris = null;
        File exportDir = TestUtils.createTempDirectory();
        String filename = "testWriteToErrorFile_nullUris.error";
        String delim = null;
        String message = null;
        testWriteToError(uris, delim, exportDir, filename, message);
        File file = new File(exportDir, filename);
        assertFalse(file.exists());
    }

    @Test
    public void testWriteToErrorFile_emptyUris() throws CorbException, IOException {
        String[] uris = new String[]{};
        File exportDir = TestUtils.createTempDirectory();
        String filename = "testWriteToErrorFile_emptyUris.error";
        String delim = null;
        String message = null;
        File errorFile = testWriteToError(uris, delim, exportDir, filename, message);

        assertFalse(errorFile.exists());
    }

    @Test
    public void testWriteToErrorFile_nullErrorFilename() throws CorbException, IOException {
        String[] uris = new String[]{"uri1"};
        File exportDir = TestUtils.createTempDirectory();
        String filename = null;
        String delim = null;
        String message = "ERROR";
        File errorFile = testWriteToError(uris, delim, exportDir, filename, message);
        assertNull(errorFile);
    }

    @Test
    public void testWriteToErrorFile_emptyErrorFilename() throws CorbException, IOException {
        String[] uris = new String[]{"uri1"};
        File exportDir = TestUtils.createTempDirectory();
        String filename = "";
        String delim = null;
        String message = "ERROR";
        File errorFile = testWriteToError(uris, delim, exportDir, filename, message);
        //testWriteToError constructs a File object that is the containing directory when filename is blank
        assertFalse(errorFile.isFile());
    }

    @Test
    public void testWriteToErrorFile_nullBatchUridelim() throws CorbException, IOException {
        String[] uris = new String[]{"uri1"};
        File exportDir = TestUtils.createTempDirectory();
        String filename = "testWriteToErrorFile_nullBatchUridelim.err";
        String delim = null;
        String message = "ERROR";
        File errorFile = testWriteToError(uris, delim, exportDir, filename, message);
        assertTrue(TestUtils.readFile(errorFile).contains(Manager.DEFAULT_BATCH_URI_DELIM));
    }

    @Test
    public void testWriteToErrorFile_emptyBatchUridelim() throws CorbException, IOException {
        String[] uris = new String[]{"uri1"};
        File exportDir = TestUtils.createTempDirectory();
        String filename = "testWriteToErrorFile_emptyBatchUridelim.err";
        String delim = null;
        String message = "ERROR";
        File errorFile = testWriteToError(uris, delim, exportDir, filename, message);
        assertTrue(TestUtils.readFile(errorFile).contains(Manager.DEFAULT_BATCH_URI_DELIM));
    }

    @Test
    public void testWriteToErrorFile_customBatchUridelim() throws CorbException, IOException {
        String[] uris = new String[]{"uri1"};
        File exportDir = TestUtils.createTempDirectory();
        String filename = "testWriteToErrorFile_customBatchUridelim.err";
        String delim = "$";
        String message = "ERROR";
        File errorFile = testWriteToError(uris, delim, exportDir, filename, message);

        assertTrue(TestUtils.readFile(errorFile).contains(delim));
    }

    @Test
    public void testWriteToErrorFile_nullMessage() throws CorbException, IOException {
        String[] uris = new String[]{"uri1"};
        File exportDir = TestUtils.createTempDirectory();
        String filename = "testWriteToErrorFile_customBatchUridelim.err";
        String delim = "$";
        String message = null;
        File errorFile = testWriteToError(uris, delim, exportDir, filename, message);
        assertFalse(TestUtils.readFile(errorFile).contains(delim));
    }

    @Test
    public void testWriteToErrorFile_emptyMessage() throws CorbException, IOException {
        String[] uris = new String[]{"uri1"};
        File exportDir = TestUtils.createTempDirectory();
        String filename = "testWriteToErrorFile_customBatchUridelim.err";
        String delim = "$";
        String message = "";
        File errorFile = testWriteToError(uris, delim, exportDir, filename, message);

        assertFalse(TestUtils.readFile(errorFile).contains(delim));
    }

    /**
     * Test of asString method, of class AbstractTask.
     */
    @Test
    public void testAsString() {
        System.out.println("asString");
        String[] uris = new String[]{"foo", "bar", "baz"};
        AbstractTask instance = new AbstractTaskImpl();
        String result = instance.asString(uris);
        assertEquals("foo,bar,baz", result);
    }

    @Test
    public void testAsString_emptyArray() {
        System.out.println("asString");
        String[] uris = new String[]{};
        AbstractTask instance = new AbstractTaskImpl();
        String result = instance.asString(uris);
        assertEquals("", result);
    }

    @Test
    public void testAsString_null() {
        System.out.println("asString");
        String[] uris = null;
        AbstractTask instance = new AbstractTaskImpl();
        String result = instance.asString(uris);
        assertEquals("", result);
    }

    /**
     * Test of cleanup method, of class AbstractTask.
     */
    @Test
    public void testCleanup() {
        System.out.println("cleanup");
        AbstractTask instance = new AbstractTaskImpl();
        instance.cs = mock(ContentSource.class);
        instance.moduleType = "moduleType";
        instance.moduleUri = "moduleUri";
        instance.properties = new Properties();
        instance.inputUris = new String[]{};
        instance.adhocQuery = "adhocQuery";
        instance.cleanup();
        assertNull(instance.cs);
        assertNull(instance.moduleType);
        assertNull(instance.moduleUri);
        assertNull(instance.properties);
        assertNull(instance.inputUris);
        assertNull(instance.adhocQuery);
    }

    /**
     * Test of getProperty method, of class AbstractTask.
     */
    @Test
    public void testGetProperty() {
        System.out.println("getProperty");
        String key = "INIT-TASK";
        String val = "foo";
        Properties props = new Properties();
        props.setProperty(key, val);
        AbstractTask instance = new AbstractTaskImpl();
        instance.properties = props;
        String result = instance.getProperty(key);
        assertEquals(val, result);
    }

    @Test
    public void testGetProperty_systemPropertyTakesPrecedence() {
        System.out.println("getProperty");
        String key = "INIT-TASK";
        String val = "foo";
        System.setProperty(key, val);
        Properties props = new Properties();
        props.setProperty(key, "bar");
        AbstractTask instance = new AbstractTaskImpl();
        instance.properties = props;
        String result = instance.getProperty(key);
        assertEquals(val, result);
        clearSystemProperties();
    }

    /**
     * Test of getValueAsBytes method, of class AbstractTask.
     */
    @Test
    public void testGetValueAsBytes_xdmBinary() {
        System.out.println("getValueAsBytes");
        XdmItem item = mock(XdmBinary.class);

        AbstractTask instance = new AbstractTaskImpl();
        byte[] result = instance.getValueAsBytes(item);
        assertNull(result);
    }

    @Test
    public void testGetValueAsBytes_xdmItem() {
        System.out.println("getValueAsBytes");
        XdmItem item = mock(XdmItem.class);
        String value = "foo";
        when(item.asString()).thenReturn(value);
        AbstractTask instance = new AbstractTaskImpl();
        byte[] result = instance.getValueAsBytes(item);
        Assert.assertArrayEquals(value.getBytes(), result);
    }

    @Test
    public void testGetValueAsBytes_default() {
        System.out.println("getValueAsBytes");
        XdmItem item = null;
        String value = "foo";
        AbstractTask instance = new AbstractTaskImpl();
        byte[] result = instance.getValueAsBytes(item);
        Assert.assertArrayEquals(new byte[]{}, result);
    }

    public class AbstractTaskImpl extends AbstractTask {

        @Override
        public String processResult(ResultSequence seq) throws CorbException {
            return "";
        }

        @Override
        public String[] call() throws Exception {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

}
