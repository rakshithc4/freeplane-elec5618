package org.freeplane.features.format;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.List;

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
        calendar.setTime((FormattedDate) obj);
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
        calendar.setTime((FormattedDate) obj);
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
        calendar.setTime((FormattedDate) obj);
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
        calendar.setTime((FormattedDate) obj);
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
    public void testScannerConstructor() {
        String[] locales = {"en_AU.UTF-8", "en_US.UTF-8"};
        Scanner sc = new Scanner(locales, false);

        assertFalse(sc.isDefault());
        List<String> actualLocales = sc.getLocales();
        assertEquals(locales.length, actualLocales.size());
        for (int i = 0; i < locales.length; i++) {
            assertEquals(locales[i], actualLocales.get(i));
        }

    }

    @Test
    public void testScannerAddParser() {
        String[] locales = {"en_AU.UTF-8", "en_US.UTF-8"};
        Scanner sc = new Scanner(locales, false);
        
        // Create the DateFormatParser and add it to the Scanner
        DateFormatParser parser = new DateFormatParser("dd/MM/yyyy");
        sc.addParser(parser);

        List<Parser> actualParsers = sc.getParsers();
        assertEquals(1, actualParsers.size());
        assertEquals(parser, actualParsers.get(0));
    }

    @Test
    public void testScannerParse() {
        String[] locales = {"en_AU.UTF-8", "en_US.UTF-8"};
        Scanner sc = new Scanner(locales, false);
        DateFormatParser parser = new DateFormatParser("dd/MM/yyyy, HH:mm:ss");
        sc.addParser(parser);
        Object obj;
        Calendar calendar = Calendar.getInstance();

        // Note: Scanner will return the string as-is if not parsed successfully

        // Test null string
        obj = sc.parse(null);
        assertNull(obj);

        // Test empty string
        obj = sc.parse("");
        assertEquals("", obj);

        // Test non-date string
        String notDate = "not a date string";
        obj = sc.parse(notDate);
        assertEquals(notDate, obj);

        // Test malformed date string
        String badDate = "12/09/2026, 01:12:60"; // bad seconds
        obj = sc.parse(badDate);
        assertEquals(badDate, obj);

        // Test valid date string 1
        String goodDate = "19/06/2018, 21:21:56";
        obj = sc.parse(goodDate);
        assertTrue(obj instanceof FormattedDate);
        calendar.setTime((FormattedDate) obj);
        assertEquals(2018, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.JUNE, calendar.get(Calendar.MONTH));
        assertEquals(19, calendar.get(Calendar.DATE));
        assertEquals(21, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(21, calendar.get(Calendar.MINUTE));
        assertEquals(56, calendar.get(Calendar.SECOND));

        // Test valid date string 2
        goodDate = "29/02/2020, 23:59:59";
        obj = sc.parse(goodDate);
        assertTrue(obj instanceof FormattedDate);
        calendar.setTime((FormattedDate) obj);
        assertEquals(2020, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.FEBRUARY, calendar.get(Calendar.MONTH));
        assertEquals(29, calendar.get(Calendar.DATE));
        assertEquals(23, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(59, calendar.get(Calendar.MINUTE));
        assertEquals(59, calendar.get(Calendar.SECOND));

        // Test valid date string 3
        goodDate = "31/12/2020, 23:59:59";
        obj = sc.parse(goodDate);
        assertTrue(obj instanceof FormattedDate);
        calendar.setTime((FormattedDate) obj);
        assertEquals(2020, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.DECEMBER, calendar.get(Calendar.MONTH));
        assertEquals(31, calendar.get(Calendar.DATE));
        assertEquals(23, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(59, calendar.get(Calendar.MINUTE));
        assertEquals(59, calendar.get(Calendar.SECOND));

        // Test bad date string 2 (29/2 in non-leap year)
        badDate = "29/02/2019, 23:59:59";
        obj = sc.parse(badDate);
        assertEquals(badDate, obj);
    }
}
