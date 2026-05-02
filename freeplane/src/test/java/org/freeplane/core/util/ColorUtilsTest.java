package org.freeplane.core.util;

import static org.junit.Assert.*;

import java.awt.Color;

import org.freeplane.features.mode.Controller;
import org.freeplane.main.application.ApplicationResourceController;
import org.junit.Before;
import org.junit.Test;

public class ColorUtilsTest {
    @Before
    public void setupForTests() {
        // IMPORTANT: Create necessary temporary infrastructure objects.
        // Create a central resource controller
        Controller freeplaneController = new Controller(new ApplicationResourceController());

        // Update the current central controller
        Controller.setCurrentController(freeplaneController);
    }
    
    @Test
    public void testNullColor() {
        Color c = null;
        assertNull(ColorUtils.colorToRGBPercentString(c));
    }

    @Test
    public void testOpaqueColor() {
        Color red = new Color(255, 0, 0);
        assertEquals("#ff0000", ColorUtils.colorToRGBPercentString(red));

        Color green = new Color(0.0f, 1.0f, 0.0f, 1.0f);
        assertEquals("#00ff00", ColorUtils.colorToRGBPercentString(green));

        Color blue = new Color(0, 0, 255, 255);
        assertEquals("#0000ff", ColorUtils.colorToRGBPercentString(blue));

        Color c = new Color(196, 39, 180);
        assertEquals("#c427b4", ColorUtils.colorToRGBPercentString(c));
    }

    @Test
    public void testTransparentColor() {
        Color red = new Color(255, 0, 0, 0);
        assertEquals("transparent", ColorUtils.colorToRGBPercentString(red));

        Color green = new Color(0f, 1f, 0f, 0f);
        assertEquals("transparent", ColorUtils.colorToRGBPercentString(green));

        Color blue = new Color(0, 0, 255, 0);
        assertEquals("transparent", ColorUtils.colorToRGBPercentString(blue));

        Color c = new Color(196, 39, 180, 0);
        assertEquals("transparent", ColorUtils.colorToRGBPercentString(c));
    }

    @Test
    public void testPartiallyTransparentColor() {
        Color red = new Color(1f, 0f, 0f, 0.5f);
        assertEquals("#ff0000, 50%", ColorUtils.colorToRGBPercentString(red));

        Color green = new Color(0f, 1f, 0f, 0.63f);
        assertEquals("#00ff00, 63%", ColorUtils.colorToRGBPercentString(green));

        Color blue = new Color(0, 0, 255, 254);
        assertEquals("#0000ff, 99%", ColorUtils.colorToRGBPercentString(blue));

        Color c = new Color(196, 39, 180, 3);
        assertEquals("#c427b4,  1%", ColorUtils.colorToRGBPercentString(c));
    }
}
