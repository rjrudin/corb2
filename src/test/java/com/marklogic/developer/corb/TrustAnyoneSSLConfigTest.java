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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.net.ssl.SSLContext;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Mads Hansen, MarkLogic Corporation
 */
public class TrustAnyoneSSLConfigTest {
    
    public TrustAnyoneSSLConfigTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getSSLContext method, of class TrustAnyoneSSLConfig.
     */
    @Test
    public void testGetSSLContext() throws Exception {
        System.out.println("getSSLContext");
        TrustAnyoneSSLConfig instance = new TrustAnyoneSSLConfig();
        SSLContext result = instance.getSSLContext();
        assertNotNull(result);
    }

    /**
     * Test of getEnabledCipherSuites method, of class TrustAnyoneSSLConfig.
     */
    @Test
    public void testGetEnabledCipherSuites() {
        System.out.println("getEnabledCipherSuites");
        TrustAnyoneSSLConfig instance = new TrustAnyoneSSLConfig();
        String[] result = instance.getEnabledCipherSuites();
        assertNull(result);
    }

    /**
     * Test of getEnabledProtocols method, of class TrustAnyoneSSLConfig.
     */
    @Test
    public void testGetEnabledProtocols() {
        System.out.println("getEnabledProtocols");
        TrustAnyoneSSLConfig instance = new TrustAnyoneSSLConfig();
        String[] result = instance.getEnabledProtocols();
        assertNull(result);
    }

}
