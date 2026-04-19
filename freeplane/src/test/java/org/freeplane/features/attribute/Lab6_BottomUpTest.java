package org.freeplane.features.attribute;

// ✅ JUnit 4 imports
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.lang.reflect.Constructor;

/**
 * =====================================================================
 * ELEC5618 Assignment 2 - Lab 6: Bottom-Up Integration Testing
 * =====================================================================
 * Three-level bottom-up class hierarchy:
 *
 *   LEVEL 1 (lowest):  Attribute
 *                          ↓ used by
 *   LEVEL 2:           NodeAttributeTableModel
 *                          ↓ used by
 *   LEVEL 3:           AttributeRegistryElement
 *
 * Strategy:
 *   - First verify Level 1 (Attribute) standalone contract
 *   - Then verify Level 2 (NodeAttributeTableModel) using Level 1 objects
 *   - Finally verify Level 3 (AttributeRegistryElement) using Levels 1+2
 *
 * All tests are self-contained: only compile + run needed, no app launch.
 * =====================================================================
 */
public class Lab6_BottomUpTest {

    private NodeAttributeTableModel model;

    @Before
    public void setUp() {
        model = new NodeAttributeTableModel(10);
    }

    // ================================================================
    // LEVEL 1: Attribute
    // Verify the basic contract that Level 2 depends on:
    //   - name and value are accessible after construction
    //   - setValue/setName work correctly
    // ================================================================

    @Test
    public void level1_Attribute_NameAndValueAccessibleAfterConstruction() {
        Attribute a = new Attribute("host", "localhost");

        // Level 2 calls getName() and getValue() — confirm they work
        assertNotNull(a.getName());
        assertNotNull(a.getValue());
        assertEquals("host",      a.getName());
        assertEquals("localhost", a.getValue());

        System.out.println("Level 1 - Attribute contract: PASS ✓");
    }

    @Test
    public void level1_Attribute_SettersWorkCorrectly() {
        Attribute a = new Attribute("old", "oldVal");
        a.setName("new");
        a.setValue("newVal");

        assertEquals("new",    a.getName());
        assertEquals("newVal", a.getValue());

        System.out.println("Level 1 - Attribute setters: PASS ✓");
    }

    // ================================================================
    // LEVEL 2: NodeAttributeTableModel uses Attribute
    // Tests that the model correctly stores, retrieves, counts,
    // and indexes Attribute objects created at Level 1.
    // ================================================================

    @Test
    public void level2_Model_StoresAndRetrievesAttributeByIndex() {
        // Build from Level 1 up
        Attribute attr = new Attribute("version", "1.0");
        model.getAttributes().add(attr);

        Attribute retrieved = model.getAttribute(0);
        assertEquals("version", retrieved.getName());
        assertEquals("1.0",     retrieved.getValue());

        System.out.println("Level 2 - store and retrieve: PASS ✓");
    }

    @Test
    public void level2_Model_RowCountReflectsNumberOfAttributes() {
        assertEquals(0, model.getRowCount());

        model.getAttributes().add(new Attribute("x", "1"));
        assertEquals(1, model.getRowCount());

        model.getAttributes().add(new Attribute("y", "2"));
        assertEquals(2, model.getRowCount());

        System.out.println("Level 2 - row count: PASS ✓");
    }

    @Test
    public void level2_Model_GetAttributeIndexFindsCorrectPosition() {
        model.getAttributes().add(new Attribute("alpha", "1"));  // index 0
        model.getAttributes().add(new Attribute("beta",  "2"));  // index 1
        model.getAttributes().add(new Attribute("gamma", "3"));  // index 2

        assertEquals(0,  model.getAttributeIndex("alpha"));
        assertEquals(1,  model.getAttributeIndex("beta"));
        assertEquals(2,  model.getAttributeIndex("gamma"));
        assertEquals(-1, model.getAttributeIndex("delta")); // not found

        System.out.println("Level 2 - getAttributeIndex: PASS ✓");
    }

    @Test
    public void level2_Model_GetValueAtReturnsNameInCol0_ValueInCol1() {
        model.getAttributes().add(new Attribute("project", "freeplane"));

        assertEquals("project",   model.getValueAt(0, 0)); // column 0 = name
        assertEquals("freeplane", model.getValueAt(0, 1)); // column 1 = value

        System.out.println("Level 2 - getValueAt columns: PASS ✓");
    }

    @Test
    public void level2_Model_GetValueAtReturnsNullWhenEmpty() {
        // Model with size 0 never allocates the internal vector
        NodeAttributeTableModel emptyModel = new NodeAttributeTableModel(0);
        assertNull(emptyModel.getValueAt(0, 0));

        System.out.println("Level 2 - getValueAt empty model: PASS ✓");
    }

    @Test
    public void level2_Model_GetAttributeKeyListReturnsAllNamesInOrder() {
        model.getAttributes().add(new Attribute("a", "1"));
        model.getAttributes().add(new Attribute("b", "2"));
        model.getAttributes().add(new Attribute("c", "3"));

        java.util.List<String> keys = model.getAttributeKeyList();

        assertEquals(3,   keys.size());
        assertEquals("a", keys.get(0));
        assertEquals("b", keys.get(1));
        assertEquals("c", keys.get(2));

        System.out.println("Level 2 - getAttributeKeyList: PASS ✓");
    }

    // ================================================================
    // LEVEL 3: AttributeRegistryElement builds on Attribute + Model
    // Tests state management: visibility, restriction, manual flags.
    // Also tests the full chain: Attribute → Model → RegistryElement.
    // ================================================================

    @Test
    public void level3_RegistryElement_InitialStateAllFalse() {
        AttributeRegistryElement element = createRegistryElement("environment");

        assertFalse("New element must NOT be visible",    element.isVisible());
        assertFalse("New element must NOT be restricted", element.isRestricted());
        assertFalse("New element must NOT be manual",     element.isManual());
        assertEquals("environment", element.getKey());

        System.out.println("Level 3 - registry element initial state: PASS ✓");
    }

    @Test
    public void level3_RegistryElement_SetManualTrue() {
        AttributeRegistryElement element = createRegistryElement("owner");
        element.setManual(true);
        assertTrue(element.isManual());

        System.out.println("Level 3 - setManual(true): PASS ✓");
    }

    @Test
    public void level3_RegistryElement_SetManualFalse() {
        AttributeRegistryElement element = createRegistryElement("owner");
        element.setManual(true);
        element.setManual(false);
        assertFalse(element.isManual());

        System.out.println("Level 3 - setManual(false): PASS ✓");
    }

    @Test
    public void level3_FullChain_AttributeInModelMatchesRegistryElementKey() {
        // ── Level 1: create the attribute ───────────────────────────
        Attribute attr = new Attribute("region", "us-east");

        // ── Level 2: store in model ──────────────────────────────────
        model.getAttributes().add(attr);

        // ── Level 3: create registry element for the same key ────────
        AttributeRegistryElement regElement = createRegistryElement("region");
        regElement.setManual(true);

        // Verify all three levels are consistent:

        // Level 2 can find the attribute
        int idx = model.getAttributeIndex("region");
        assertEquals("Attribute 'region' should be at index 0", 0, idx);

        // Level 2 returns correct value
        assertEquals("us-east", model.getValueAt(0, 1));

        // Level 3 describes the same key
        assertEquals("region", regElement.getKey());
        assertTrue(regElement.isManual());

        System.out.println("Level 3 - full chain integration: PASS ✓");
    }

    // ── Helper ───────────────────────────────────────────────────────

    /**
     * Creates an AttributeRegistryElement without a real AttributeRegistry.
     * Uses reflection to access the package-private constructor so the test
     * remains fully self-contained (no running Freeplane app needed).
     */
    private AttributeRegistryElement createRegistryElement(String key) {
        try {
            Constructor<AttributeRegistryElement> ctor =
                AttributeRegistryElement.class
                    .getDeclaredConstructor(AttributeRegistry.class, String.class);
            ctor.setAccessible(true);
            // Pass null for registry — only state methods (isManual, isVisible,
            // isRestricted, getKey) are called; none fire back to the registry.
            return ctor.newInstance(null, key);
        } catch (Exception e) {
            throw new RuntimeException(
                "Could not instantiate AttributeRegistryElement: " + e.getMessage(), e);
        }
    }
}