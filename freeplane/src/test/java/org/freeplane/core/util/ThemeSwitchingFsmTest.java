package org.freeplane.core.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.freeplane.core.ui.components.UITools;
import org.junit.Before;
import org.junit.Test;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

public class ThemeSwitchingFsmTest {

    private enum ThemeState { LIGHT_THEME, DARK_THEME }
    private enum ThemeInput { APPLY_LIGHT, APPLY_DARK }

    private void applyTransition(ThemeInput input) {
        try {
            switch (input) {
                case APPLY_LIGHT:
                    FlatLightLaf.setup();
                    break;
                case APPLY_DARK:
                    FlatDarkLaf.setup();
                    break;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to apply theme transition: " + input, e);
        }
    }

    private ThemeState observeCurrentState() {
        return UITools.isLightLookAndFeelInstalled() ? ThemeState.LIGHT_THEME : ThemeState.DARK_THEME;
    }

    @Before
    public void startInLightTheme() throws Exception {
        FlatLightLaf.setup();
    }

    /** -------------------------------------------------------------------------
        Path 1: S0 --[APPLY_DARK]--> S1
        Precondition: light theme is active (visually: combo shows "Flat Light")
        Input data:   user selects "Flat Dark"
        Expected:     UI transitions to DARK_THEME; background becomes dark
       ----------------------------------------------------------------------------**/
    @Test
    public void path1_lightToDark_transitionReachesDarkThemeState() {
        assertTrue("Precondition failed: expected LIGHT_THEME before transition",
                observeCurrentState() == ThemeState.LIGHT_THEME);

        applyTransition(ThemeInput.APPLY_DARK);

        assertFalse("Path 1 failed: expected isLightLookAndFeelInstalled() == false after selecting Flat Dark",
                UITools.isLightLookAndFeelInstalled());

        assertTrue("Path 1 failed: expected DARK_THEME state after APPLY_DARK transition",
                observeCurrentState() == ThemeState.DARK_THEME);
    }

    /** -------------------------------------------------------------------------
        Path 2: S1 --[APPLY_LIGHT]--> S0
        Precondition: dark theme is active, overall ui is dark
        Input data:   user selects "Flat Light"
        Expected:     background becomes light, and ui shows light theme
     ----------------------------------------------------------------------------**/

    @Test
    public void path2_darkToLight_transitionReachesLightThemeState() {
        applyTransition(ThemeInput.APPLY_DARK);
        assertTrue("Precondition failed: expected DARK_THEME before Path 2 transition",
                observeCurrentState() == ThemeState.DARK_THEME);

        applyTransition(ThemeInput.APPLY_LIGHT);

        assertTrue("Path 2 failed: expected isLightLookAndFeelInstalled() == true after selecting Flat Light",
                UITools.isLightLookAndFeelInstalled());
        assertTrue("Path 2 failed: expected LIGHT_THEME state after APPLY_LIGHT transition",
                observeCurrentState() == ThemeState.LIGHT_THEME);
    }

    /** -------------------------------------------------------------------------
        Path 3: S0 --[APPLY_DARK]--> S1 --[APPLY_LIGHT]--> S0
        Precondition: light theme active (combo shows "Flat Light")
        Input data:   Select "Flat Dark"  then  select "Flat Light"
        Expected:     dark after step 1 , then light after step 2
       ----------------------------------------------------------------------------**/
    @Test
    public void path3_lightDarkLightRoundTrip_returnsToLightThemeState() {
        assertTrue("Precondition failed: expected LIGHT_THEME at start of Path 3",
                observeCurrentState() == ThemeState.LIGHT_THEME);

        applyTransition(ThemeInput.APPLY_DARK);
        assertTrue("Path 3 step-1 failed: expected DARK_THEME after first transition",
                observeCurrentState() == ThemeState.DARK_THEME);

        applyTransition(ThemeInput.APPLY_LIGHT);
        assertTrue("Path 3 step-2 failed: expected LIGHT_THEME after round-trip",
                observeCurrentState() == ThemeState.LIGHT_THEME);

        // Check if its back to light theme
        assertTrue("Path 3 failed: final state should be LIGHT_THEME",
                UITools.isLightLookAndFeelInstalled());
    }
}
