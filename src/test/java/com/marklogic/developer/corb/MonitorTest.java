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

import static com.marklogic.developer.corb.Monitor.getProgressMessage;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Mads Hansen, MarkLogic Corporation
 */
public class MonitorTest {

    private static final double DOUBLE_DELTA = 0.0;

    public MonitorTest() {
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
     * Test of calculateThreadsPerSecond method, of class Monitor.
     */
    @Test
    public void testCalculateThreadsPerSecond_3args() {
        System.out.println("calculateThreadsPerSecond");
        long amountCompleted = 10L;
        long previousMillis = 1000L;
        long currentMillis = 2000L;
        double expResult = Monitor.calculateThreadsPerSecond(amountCompleted, 0, currentMillis, previousMillis);
        double result = Monitor.calculateThreadsPerSecond(amountCompleted, currentMillis, previousMillis);
        assertEquals(expResult, result, DOUBLE_DELTA);
    }

    /**
     * Test of calculateThreadsPerSecond method, of class Monitor.
     */
    @Test
    public void testCalculateThreadsPerSecond_4args() {
        System.out.println("calculateThreadsPerSecond");
        long amountCompleted = 110L;
        long previouslyCompleted = 10L;
        long currentMillis = 2000L;
        long previousMillis = 1000L;
        double expResult = 100.0;
        double result = Monitor.calculateThreadsPerSecond(amountCompleted, previouslyCompleted, currentMillis, previousMillis);
        assertEquals(expResult, result, DOUBLE_DELTA);
    }

    /**
     * Test of calculateThreadsPerSecond method, of class Monitor.
     */
    @Test
    public void testCalculateThreadsPerSecond_fractional() {
        System.out.println("calculateThreadsPerSecond");
        long amountCompleted = 10L;
        long previouslyCompleted = 9L;
        long currentMillis = 3000L;
        long previousMillis = 1000L;
        double expResult = 0.5;
        double result = Monitor.calculateThreadsPerSecond(amountCompleted, previouslyCompleted, currentMillis, previousMillis);
        assertEquals(expResult, result, DOUBLE_DELTA);
    }

    @Test
    public void testGetProgressMessage() {
        assertEquals("10/100, 4 tps(avg), 3 tps(cur), ETC 00:00:22, 2 active threads.", getProgressMessage(10, 100, 4, 3, 2));
        assertEquals("10/100, 0.4 tps(avg), 3 tps(cur), ETC 00:03:45, 2 active threads.", getProgressMessage(10, 100, 0.4, 3, 2));
        assertEquals("10/100, 0.49 tps(avg), 3 tps(cur), ETC 00:03:03, 2 active threads.", getProgressMessage(10, 100, 0.49, 3, 2));
        assertEquals("10/100, 0.45 tps(avg), 3 tps(cur), ETC 00:03:20, 2 active threads.", getProgressMessage(10, 100, 0.449, 3, 2));
        assertEquals("10/100, 0.04 tps(avg), 3 tps(cur), ETC 00:34:05, 2 active threads.", getProgressMessage(10, 100, 0.044, 3, 2));
        assertEquals("10/100, 0 tps(avg), 3 tps(cur), ETC 06:15:00, 2 active threads.", getProgressMessage(10, 100, 0.004, 3, 2));
    }

    @Test
    public void testGetEstimatedTimeCompletion_zero() {
        assertEquals("00:00:-1", Monitor.getEstimatedTimeCompletion(100, 50, 0));
    }

    @Test
    public void testGetEstimatedTimeCompletion() {
        assertEquals("02:38:20", Monitor.getEstimatedTimeCompletion(100, 5, 0.01));
        assertEquals("01:23:20", Monitor.getEstimatedTimeCompletion(100, 50, 0.01));
        assertEquals("00:08:20", Monitor.getEstimatedTimeCompletion(100, 50, 0.1));
        assertEquals("00:00:50", Monitor.getEstimatedTimeCompletion(100, 50, 1.0));
        assertEquals("00:00:45", Monitor.getEstimatedTimeCompletion(100, 50, 1.1));
        assertEquals("00:00:45", Monitor.getEstimatedTimeCompletion(100, 50, 1.111));
        assertEquals("00:00:44", Monitor.getEstimatedTimeCompletion(100, 50, 1.12345));
        assertEquals("00:00:01", Monitor.getEstimatedTimeCompletion(100, 50, 49));
        assertEquals("00:00:00", Monitor.getEstimatedTimeCompletion(100, 50, 60));

        assertEquals("2777:38:20", Monitor.getEstimatedTimeCompletion(100000d, 5d, 0.01d));
    }

    @Test
    public void testFormat() {
        assertEquals("1", Monitor.formatTransactionsPerSecond(1));
        assertEquals("0.9", Monitor.formatTransactionsPerSecond(0.9));
        assertEquals("0.95", Monitor.formatTransactionsPerSecond(0.95));
        assertEquals("0.96", Monitor.formatTransactionsPerSecond(0.955));
        assertEquals("0.96", Monitor.formatTransactionsPerSecond(0.9559));
        assertEquals("0.01", Monitor.formatTransactionsPerSecond(0.01));
        assertEquals("0.01", Monitor.formatTransactionsPerSecond(0.014));
        assertEquals("100", Monitor.formatTransactionsPerSecond(100.00));
        assertEquals("1,000", Monitor.formatTransactionsPerSecond(1000));
        assertEquals("100", Monitor.formatTransactionsPerSecond(100.1234));
        assertEquals("100", Monitor.formatTransactionsPerSecond(100.999));
    }
}
