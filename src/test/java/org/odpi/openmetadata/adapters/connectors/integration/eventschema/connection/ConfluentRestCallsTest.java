package org.odpi.openmetadata.adapters.connectors.integration.eventschema.connection;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ConfluentRestCallsTest {

    private ConfluentRestCalls confluentRestCalls = new ConfluentRestCalls("", "", "");

    @Nested
    @DisplayName("Testcases for ConfluentRestCalls.convertResponseToStringArray with list of Strings")
    class ConverTest {
        final String subject1 = "test";
        final String subject2 = "subject";
        @Test
        void testConvertNull() {
            List<String> result = confluentRestCalls.convertResponseToStringArray(null);
            assertNotNull(result, "#convertStringToList() should return a list, but null returned");
        }

        @Test
        void testConvertEmpty() {
            List<String> result = confluentRestCalls.convertResponseToStringArray("");
            assertNotNull(result, "#convertStringToList() should return a list, but null returned");
        }

        @Test
        void testConvertOne() {
            String restCallResult = "[\"" + subject1 + "\"]";
            List<String> result = confluentRestCalls.convertResponseToStringArray(restCallResult);
            assertNotNull(result, "#convertStringToList() should return a list, but null returned");
            assertEquals(1, result.size(), "list of length 1 excepted");
            assertEquals(subject1, result.get(0), "Extracted subject name does not match");
        }

        @Test
        void testConvertTwo() {
            String restCallResult = "[\""+subject1+"\",\""+subject2+"\"]";
            List<String> result = confluentRestCalls.convertResponseToStringArray(restCallResult);
            assertNotNull(result, "#convertStringToList() should return a list, but null returned");
            assertEquals(2, result.size(), "list of length 1 excepted");
            assertEquals(subject1, result.get(0), "Extracted subject name does not match");
            assertEquals(subject2, result.get(1), "Extracted subject name does not match");
        }
    }

    @Nested
    @DisplayName("Testcases for ConfluentRestCalls.getVersionsOfSubject with list of numbers")
    class VersionsTest {
        final Integer version1 = 1;
        final Integer version2 = 2;
        @Test
        void testConvertNull() {
            List<String> result = confluentRestCalls.convertResponseToStringArray(null);
            assertNotNull(result, "#convertStringToList() should return a list, but null returned");
        }

        @Test
        void testConvertEmpty() {
            List<String> result = confluentRestCalls.convertResponseToStringArray("");
            assertNotNull(result, "#convertStringToList() should return a list, but null returned");
        }

        @Test
        void testConvertOne() {
            String restCallResult = "[" + version1 + "]";
            List<String> result = confluentRestCalls.convertResponseToStringArray(restCallResult);
            assertNotNull(result, "#convertStringToList() should return a list, but null returned");
            assertEquals(1, result.size(), "list of length 1 excepted");
            assertEquals(version1.toString(), result.get(0), "Extracted subject name does not match");
        }

        @Test
        void testConvertTwo() {
            String restCallResult = "["+version1+","+version2+"]";
            List<String> result = confluentRestCalls.convertResponseToStringArray(restCallResult);
            assertNotNull(result, "#convertStringToList() should return a list, but null returned");
            assertEquals(2, result.size(), "list of length 1 excepted");
            assertEquals(version1.toString(), result.get(0), "Extracted subject name does not match");
            assertEquals(version2.toString(), result.get(1), "Extracted subject name does not match");
        }
    }
}
