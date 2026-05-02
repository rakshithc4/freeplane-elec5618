package org.freeplane.features.format;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import org.freeplane.features.mode.Controller;
import org.freeplane.main.application.ApplicationResourceController;
import org.junit.Before;
import org.junit.Test;

/**
 * This class is used to test the FormattedDate class.
 */
public class FormattedDateTest {
    @Before
    public void setupForTests() {
        // IMPORTANT: Create necessary temporary infrastructure objects.
        // Create a central resource controller
        Controller freeplaneController = new Controller(new ApplicationResourceController());

        // Add the format controller extension
        freeplaneController.addExtension(FormatController.class, new FormatController());

        // Update the current central controller
        Controller.setCurrentController(freeplaneController);
    }

    @Test
    public void testConstructors() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();

        // TEST: constructor with long int argument
        FormattedDate fd1 = new FormattedDate(0, simpleDateFormat);

        assertEquals(simpleDateFormat, fd1.getDateFormat());
        assertEquals(new Date(0).getTime(), fd1.getTime());

        // TEST: copying constructor
        FormattedDate fd2 = new FormattedDate(fd1);
        assertEquals(fd1, fd2);
        assertEquals(fd1.getDateFormat(), fd2.getDateFormat());

        FormattedDate fd3 = new FormattedDate(0, null);
        FormattedDate fd4 = new FormattedDate(fd3);
        assertNull(fd4.getDateFormat());
        assertEquals(fd3.getDateFormat(), fd4.getDateFormat());

        // TEST: constructor with Date argument
        Date d1 = Date.from(Instant.now());
        FormattedDate fd5 = new FormattedDate(d1, FormattedDate.ISO_DATE_FORMAT_PATTERN);

        assertEquals(d1.getTime(), fd5.getTime());
        assertEquals(FormatController.getController().getDateFormat(FormattedDate.ISO_DATE_FORMAT_PATTERN),
                fd5.getDateFormat());
    }

    @Test
    public void testFactoryMethod() {
        // Test the static factory method that is used to create
        // new FormattedDate objects.
        long timeNow = Instant.now().toEpochMilli();
        FormattedDate fd = FormattedDate.createDefaultFormattedDate(timeNow, IFormattedObject.TYPE_DATE);

        assertEquals(timeNow, fd.getTime());
        assertEquals(FormatController.getController().getDefaultDateFormat(), fd.getDateFormat());
    }

    @Test
    public void testToString() {
        // Test toString() with different dates and formats
        FormattedDate fd1 = new FormattedDate(0, new SimpleDateFormat());
        assertEquals("1/1/70, 10:00\u202fAM", fd1.toString());

        FormattedDate fd2 = FormattedDate.createDefaultFormattedDate(0, IFormattedObject.TYPE_DATE);
        assertEquals("1/1/70", fd2.toString());
    }

    @Test
    public void testSerialize() {
        // Test serialising different date formats
        FormattedDate fd1 = FormattedDate.createDefaultFormattedDate(0, IFormattedObject.TYPE_DATE);
        String result = FormattedDate.serialize(fd1);
        assertEquals("1970-01-01T10:00+1000|date", result);

        FormattedDate fd2 = new FormattedDate(1000000, new SimpleDateFormat());
        result = FormattedDate.serialize(fd2);
        assertEquals("1970-01-01T10:16+1000|M/d/yy, h:mm\u202fa", result);
    }

    @Test
    public void testToStringISO() {
        // Test converting dates in different formats to ISO strings
        Date date = new Date(0);
        String result = FormattedDate.toStringISO(date);
        assertEquals("1970-01-01T10:00+1000", result);

        date = new Date(318413434);
        result = FormattedDate.toStringISO(date);
        assertEquals("1970-01-05T02:26+1000", result);
    }

    @Test
    public void testToStringShortISO() {
        // Test converting dates in different formats to short ISO strings
        Date date = new Date(0);
        String result = FormattedDate.toStringShortISO(date);
        assertEquals("1970-01-01", result);

        date = new Date(100000000);
        result = FormattedDate.toStringShortISO(date);
        assertEquals("1970-01-02", result);
    }

    @Test
    public void testDeserialise() {
        String str1 = "1970-01-01T10:00+1000|date";
        FormattedDate fd1 = FormattedDate.createDefaultFormattedDate(0, IFormattedObject.TYPE_DATE);
        Object result = FormattedDate.deserialize(str1);
        assertEquals(fd1.getTime(), ((FormattedDate) result).getTime());
        assertEquals(fd1, result);

        String dateFormatString = "M/d/yy, hh:mm:ss";
        String str2 = "1970-01-01T10:16+1000|" + dateFormatString;
        FormattedDate fd2 = new FormattedDate(960000, new SimpleDateFormat(dateFormatString));
        result = FormattedDate.deserialize(str2);
        assertEquals(fd2.getTime(), ((FormattedDate) result).getTime());
        assertEquals(fd2, result);
    }

    @Test
    public void testToDateAndToDateISO() {
        // The two methods toDate() and toDateISO() currently have
        // the same implementation.
        String isoString = "1970-01-01T10:16+1000";
        FormattedDate fd1 = FormattedDate.toDateISO(isoString);
        FormattedDate fd2 = FormattedDate.toDate(isoString);
        FormattedDate expected = new FormattedDate(new Date(960000), FormattedDate.ISO_DATE_TIME_FORMAT_PATTERN);

        assertEquals(expected, fd1);
        assertEquals(expected, fd2);

        isoString = "1970-01-01T10:00+1000";
        fd1 = FormattedDate.toDateISO(isoString);
        fd2 = FormattedDate.toDate(isoString);
        expected = new FormattedDate(new Date(0), FormattedDate.ISO_DATE_TIME_FORMAT_PATTERN);

        assertEquals(expected, fd1);
        assertEquals(expected, fd2);
    }

    @Test
    public void testIsDate() {
        // Normal correct ISO String
        String isoString = "2026-01-01T12:25+1000";
        assertTrue(FormattedDate.isDate(isoString));

        // Missing one digit in the number of seconds
        String notIsoString = "1970-01-01T10:0+1000";
        assertFalse(FormattedDate.isDate(notIsoString));

        // Null string
        assertFalse(FormattedDate.isDate(null));
    }

    @Test
    public void testContainsTime() {
        // Contains time in the format
        FormattedDate fd1 = new FormattedDate(0, new SimpleDateFormat("M/d/yy, hh:mm:ss"));
        assertTrue(fd1.containsTime());

        // Does not contains time in the format
        FormattedDate fd2 = new FormattedDate(0, new SimpleDateFormat("M/d/yy"));
        assertFalse(fd2.containsTime());
    }

    @Test
    public void testGetDateFormat() {
        // Check that the date format field is set correctly
        SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yy, hh:mm:ss");
        FormattedDate fd = new FormattedDate(0, dateFormat);

        assertEquals(dateFormat, fd.getDateFormat());
    }

    @Test
    public void testGetPattern() {
        // Check that the correct date format pattern is returned
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        FormattedDate fd = new FormattedDate(132, dateFormat);

        assertEquals(dateFormat.toPattern(), fd.getDateFormat().toPattern());
    }

    @Test
    public void testGetObject() {
        long milliEpoch = 123456789;
        Date date = new Date(milliEpoch);

        FormattedDate fd1 = new FormattedDate(date, FormattedDate.ISO_DATE_TIME_FORMAT_PATTERN);
        assertEquals(date, fd1.getObject());

        FormattedDate fd2 = new FormattedDate(milliEpoch, new SimpleDateFormat());
        assertEquals(date, fd2.getObject());
    }

    @Test
    public void testEquals() {
        // Test that 2 objects with same date and same format should be equal
        String dateFormat = "M/d/yy, hh:mm:ss";
        FormattedDate fd1 = new FormattedDate(123456, new SimpleDateFormat(dateFormat));
        FormattedDate fd2 = new FormattedDate(123456, new SimpleDateFormat(dateFormat));
        assertTrue(fd1.equals(fd2));

        FormattedDate fd3 = FormattedDate.createDefaultFormattedDate(123456, IFormattedObject.TYPE_DATETIME);
        FormattedDate fd4 = FormattedDate.createDefaultFormattedDate(123456, IFormattedObject.TYPE_DATETIME);
        assertTrue(fd3.equals(fd4));

        // Test that 2 objects with same date but different formats should not be equal
        assertFalse(fd1.equals(fd3));
        assertFalse(fd2.equals(fd4));

        // Test that 2 objects with same format but different dates should not be equal
        FormattedDate fd5 = new FormattedDate(123455, new SimpleDateFormat(dateFormat));
        assertFalse(fd1.equals(fd5));
    }

    @Test
    public void testHashCode() {
        FormattedDate fd = FormattedDate.createDefaultFormattedDate(0, IFormattedObject.TYPE_DATE);
        long hashTime = fd.getTime();
        int expectedHash = 37 * ((int) hashTime ^ (int) (hashTime >> 32)) + fd.getDateFormat().hashCode();
        assertEquals(expectedHash, fd.hashCode());
    }
}
