package org.freeplane.features.attribute;

// ✅ JUnit 4 imports — matches the project's build.gradle
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * =====================================================================
 * ELEC5618 Software Quality Engineering - Assignment 2
 * Lab 5: White Box Testing
 * =====================================================================
 *
 * TARGET METHOD: NodeAttributeTableModel.getAttributeIndex(String name)
 * LOCATION: org.freeplane.features.attribute.NodeAttributeTableModel
 *
 * SOURCE CODE OF METHOD UNDER TEST:
 * ----------------------------------
 *   public int getAttributeIndex(final String name) {
 *       if (name == null) {                          // Decision D1
 *           return -1;
 *       }
 *       int pos = 0;
 *       for (final Attribute attr : getAttributes()) { // Decision D2 (loop)
 *           if (name.equals(attr.getName())) {          // Decision D3
 *               return pos;
 *           }
 *           pos++;
 *       }
 *       return -1;
 *   }
 *
 * CYCLOMATIC COMPLEXITY CALCULATION:
 * ------------------------------------
 *   Decision points:
 *     D1: if (name == null)                  → +1
 *     D2: for (final Attribute attr : ...)   → +1
 *     D3: if (name.equals(attr.getName()))   → +1
 *
 *   CC = Number of Decisions + 1
 *   CC = 3 + 1 = 4
 *
 * INDEPENDENT PATHS (4 paths matching CC = 4):
 * ----------------------------------------------
 *   Path 1: D1=true  → name is null → return -1 immediately
 *   Path 2: D1=false, D2=false → list is empty → return -1
 *   Path 3: D1=false, D2=true, D3=true → match found → return index
 *   Path 4: D1=false, D2=true, D3=false (all) → no match → return -1
 * =====================================================================
 */
public class Lab5_WhiteBoxTest {

    // The model under test — a table that holds key-value Attributes
    private NodeAttributeTableModel model;

    /**
     * Runs before every test — creates a fresh empty model
     * so tests do not interfere with each other.
     */
    @Before
    public void setUp() {
        model = new NodeAttributeTableModel(10);
    }

    // ----------------------------------------------------------------
    // PATH 1: name argument is null
    //
    // Control flow: D1 = TRUE → immediately returns -1
    // The null check fires before the loop is ever reached.
    // ----------------------------------------------------------------
    @Test
    public void testPath1_NullName_ReturnsMinusOne() {
        // Arrange: populate the model so it is not empty
        // (proves the null check fires BEFORE the loop)
        model.getAttributes().add(new Attribute("key1", "val1"));
        model.getAttributes().add(new Attribute("key2", "val2"));

        // Act
        int result = model.getAttributeIndex((String) null);

        // Assert
        assertEquals("Path 1 FAILED: null name must return -1", -1, result);

        // Boolean result for assessment
        boolean passed = (result == -1);
        System.out.println("Path 1 - null name test: " + (passed ? "PASS ✓" : "FAIL ✗"));
        assertTrue(passed);
    }

    // ----------------------------------------------------------------
    // PATH 2: name is not null, but attribute list is empty
    //
    // Control flow: D1=false → D2=false (loop never entered) → return -1
    // The for-each loop condition fails immediately on an empty list.
    // ----------------------------------------------------------------
    @Test
    public void testPath2_EmptyList_ReturnsMinusOne() {
        // Arrange: model with no attributes added (default state after setUp)
        // No attributes added — getAttributes() returns an empty vector

        // Act
        int result = model.getAttributeIndex("anyKey");

        // Assert
        assertEquals("Path 2 FAILED: empty list must return -1", -1, result);

        boolean passed = (result == -1);
        System.out.println("Path 2 - empty list test: " + (passed ? "PASS ✓" : "FAIL ✗"));
        assertTrue(passed);
    }

    // ----------------------------------------------------------------
    // PATH 3: name is not null, attribute IS found in the list
    //
    // Control flow: D1=false → D2=true (enter loop) → D3=true → return pos
    // Loop iterates, finds a match, and returns the correct index.
    // ----------------------------------------------------------------
    @Test
    public void testPath3_AttributeFound_ReturnsCorrectIndex() {
        // Arrange: add three attributes — we will search for the middle one
        model.getAttributes().add(new Attribute("priority", "high"));   // index 0
        model.getAttributes().add(new Attribute("status",   "open"));   // index 1
        model.getAttributes().add(new Attribute("owner",    "alice"));  // index 2

        // Act: search for "status" which should be at index 1
        int result = model.getAttributeIndex("status");

        // Assert
        assertEquals("Path 3 FAILED: 'status' should be at index 1", 1, result);

        boolean passed = (result == 1);
        System.out.println("Path 3 - attribute found test: " + (passed ? "PASS ✓" : "FAIL ✗"));
        assertTrue(passed);
    }

    // ----------------------------------------------------------------
    // PATH 4: name is not null, attribute is NOT in the list
    //
    // Control flow: D1=false → D2=true (enter loop) →
    //               D3=false (for every element) → loop ends → return -1
    // The loop runs to completion without finding a match.
    // ----------------------------------------------------------------
    @Test
    public void testPath4_AttributeNotFound_ReturnsMinusOne() {
        // Arrange: add attributes that do NOT include the key we search for
        model.getAttributes().add(new Attribute("color", "red"));
        model.getAttributes().add(new Attribute("size",  "large"));
        model.getAttributes().add(new Attribute("shape", "circle"));

        // Act: search for a key that does not exist in the list
        int result = model.getAttributeIndex("nonexistent");

        // Assert
        assertEquals("Path 4 FAILED: missing attribute must return -1", -1, result);

        boolean passed = (result == -1);
        System.out.println("Path 4 - attribute not found test: " + (passed ? "PASS ✓" : "FAIL ✗"));
        assertTrue(passed);
    }
}