package org.freeplane.features.format;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.freeplane.features.mode.Controller;
import org.freeplane.main.application.ApplicationResourceController;
import org.junit.Before;
import org.junit.Test;

/**
 * This class is used to perform bottom-up integration testing
 * for three classes: FormattedDate, DateFormatParser, and
 * Scanner.
 */
public class FormatBottomUpTest {
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
    
    /*------------------------------------------------
        PHASE 2: FormattedDate + DateFormatParser
    --------------------------------------------------*/

    @Test
    public void testDateFormatParserOneArgConstructor() {
        // Test format that has time
        String format = "M/d/yy, hh:mm:ss";
        DateFormatParser parser = new DateFormatParser(format);
        assertEquals(format, parser.getFormat());
        assertEquals(IFormattedObject.TYPE_DATETIME, parser.getType());
        assertEquals(Parser.STYLE_DATE, parser.getStyle());

        // Test format that does not have time
        format = "M/d/yy";
        parser = new DateFormatParser(format);
        assertEquals(format, parser.getFormat());
        assertEquals(IFormattedObject.TYPE_DATE, parser.getType());
        assertEquals(Parser.STYLE_DATE, parser.getStyle());
    }

    @Test
    public void testDateFormatParserTwoArgsConstructor() {
        // Test format that has time with type string
        String format = "M/d/yy, hh:mm:ss";
        DateFormatParser parser = new DateFormatParser(format, IFormattedObject.TYPE_STRING);
        assertEquals(format, parser.getFormat());
        assertEquals(IFormattedObject.TYPE_STRING, parser.getType());
        assertEquals(Parser.STYLE_DATE, parser.getStyle());

        // Test format that does not have time with type datetime
        format = "M/d/yy";
        parser = new DateFormatParser(format, IFormattedObject.TYPE_DATETIME);
        assertEquals(format, parser.getFormat());
        assertEquals(IFormattedObject.TYPE_DATETIME, parser.getType());
        assertEquals(Parser.STYLE_DATE, parser.getStyle());
    }

    @Test
    public void testParserWithTime() {
        // TEST: parser with format that has time
        String format = "M/d/yy, hh:mm:ss";
        DateFormatParser parser = new DateFormatParser(format, IFormattedObject.TYPE_DATETIME);
        Object obj;
        FormattedDate fd;
        Calendar calendar = Calendar.getInstance();
        
        // Test null string
        obj = parser.parse(null);
        assertNull(obj);

        // Test string that has leading whitespace when the format does not allow
        obj = parser.parse(" 12/25/25, 01:12:12");
        assertNull(obj);
        
        // Test valid string 1
        String dateStr = "1/1/70, 10:00:00"; // UNIX time start
        obj = parser.parse(dateStr);
        assertTrue(obj instanceof FormattedDate);
        fd = (FormattedDate) obj;
        assertEquals(0, fd.getTime());
        calendar.setTime(fd);
        assertEquals(1970, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.JANUARY, calendar.get(Calendar.MONTH));
        assertEquals(1, calendar.get(Calendar.DATE));
        assertEquals(10, calendar.get(Calendar.HOUR));
        assertEquals(0, calendar.get(Calendar.MINUTE));
        assertEquals(0, calendar.get(Calendar.SECOND));

        // Test valid string 2
        dateStr = "12/25/26, 09:26:11";
        obj = parser.parse(dateStr);
        assertTrue(obj instanceof FormattedDate);
        fd = (FormattedDate) obj;
        calendar.setTime(fd);
        assertEquals(2026, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.DECEMBER, calendar.get(Calendar.MONTH));
        assertEquals(25, calendar.get(Calendar.DATE));
        assertEquals(9, calendar.get(Calendar.HOUR));
        assertEquals(26, calendar.get(Calendar.MINUTE));
        assertEquals(11, calendar.get(Calendar.SECOND));

        // Test malformed string
        dateStr = "12/25/26";
        obj = parser.parse(dateStr);
        assertNull(obj);
    }

    @Test
    public void testParserWithoutTime() {
        // TEST: parser with format that does not have time
        String format = "dd/MMM/yyyy";
        DateFormatParser parser = new DateFormatParser(format, IFormattedObject.TYPE_DATE);
        Object obj;
        FormattedDate fd;
        Calendar calendar = Calendar.getInstance();
        
        // Test null string
        obj = parser.parse(null);
        assertNull(obj);

        // Test string that has leading whitespace when the format does not allow
        obj = parser.parse(" 23/Dec/2021");
        assertNull(obj);
        
        // Test valid string 1
        String dateStr = "01/Jan/1984";
        obj = parser.parse(dateStr);
        assertTrue(obj instanceof FormattedDate);
        fd = (FormattedDate) obj;
        calendar.setTime(fd);
        assertEquals(1984, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.JANUARY, calendar.get(Calendar.MONTH));
        assertEquals(1, calendar.get(Calendar.DATE));
        assertEquals(0, calendar.get(Calendar.HOUR));
        assertEquals(0, calendar.get(Calendar.MINUTE));
        assertEquals(0, calendar.get(Calendar.SECOND));

        // Test valid string 2
        dateStr = "26/Apr/2022";
        obj = parser.parse(dateStr);
        assertTrue(obj instanceof FormattedDate);
        fd = (FormattedDate) obj;
        calendar.setTime(fd);
        assertEquals(2022, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.APRIL, calendar.get(Calendar.MONTH));
        assertEquals(26, calendar.get(Calendar.DATE));
        assertEquals(0, calendar.get(Calendar.HOUR));
        assertEquals(0, calendar.get(Calendar.MINUTE));
        assertEquals(0, calendar.get(Calendar.SECOND));

        // Test invalid date string
        dateStr = "26/04/2026";
        obj = parser.parse(dateStr);
        assertNull(obj);
    }

    @Test
    public void testFaultyParser() {
        // TEST: faulty parser
        DateFormatParser faultyParser = new DateFormatParser("dd/MM/yyyy", "Invalid type string");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            faultyParser.parse("25/12/2026");
        });
        assertTrue(exception.getMessage().contains("unknown format"));
    }

    /*------------------------------------------------
            PHASE 3: Three classes together
    --------------------------------------------------*/

    @Test
    public void testScannerAddParser() {
        // TODO
    }

    @Test
    public void testScannerParse() {
        // TODO
    }
}
