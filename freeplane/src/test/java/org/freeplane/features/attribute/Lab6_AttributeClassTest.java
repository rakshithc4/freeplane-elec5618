package org.freeplane.features.attribute;

// ✅ JUnit 4 imports
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * =====================================================================
 * ELEC5618 Assignment 2 - Lab 6: OOP Class Testing (Step 1)
 * =====================================================================
 * Class under test: org.freeplane.features.attribute.Attribute
 *
 * Coverage:
 *   - All 3 constructors
 *   - All getters: getName(), getValue()
 *   - All setters: setName(), setValue()
 *   - Object state: isManaged(), toString(), null handling
 * =====================================================================
 */
public class Lab6_AttributeClassTest {

    // ── Constructors ─────────────────────────────────────────────────

    @Test
    public void testConstructor_NameOnly_SetsNameAndEmptyStringValue() {
        Attribute attr = new Attribute("category");
        assertEquals("category", attr.getName());
        assertEquals("", attr.getValue());
    }

    @Test
    public void testConstructor_NameAndValue_BothSetCorrectly() {
        Attribute attr = new Attribute("status", "active");
        assertEquals("status", attr.getName());
        assertEquals("active", attr.getValue());
    }

    @Test
    public void testCopyConstructor_CreatesIndependentCopy() {
        Attribute original = new Attribute("key", "value");
        Attribute copy = new Attribute(original);

        // Values match
        assertEquals(original.getName(),  copy.getName());
        assertEquals(original.getValue(), copy.getValue());

        // But they are different objects
        assertNotSame(original, copy);
    }

    // ── Getters ──────────────────────────────────────────────────────

    @Test
    public void testGetName_ReturnsCorrectName() {
        Attribute attr = new Attribute("myKey", "myVal");
        assertEquals("myKey", attr.getName());
    }

    @Test
    public void testGetValue_ReturnsCorrectValue() {
        Attribute attr = new Attribute("k", "v");
        assertEquals("v", attr.getValue());
    }

    // ── Setters ──────────────────────────────────────────────────────

    @Test
    public void testSetName_UpdatesNameSuccessfully() {
        Attribute attr = new Attribute("oldName", "val");
        attr.setName("newName");
        assertEquals("newName", attr.getName());
    }

    @Test
    public void testSetValue_UpdatesValueSuccessfully() {
        Attribute attr = new Attribute("key", "oldValue");
        attr.setValue("newValue");
        assertEquals("newValue", attr.getValue());
    }

    @Test
    public void testSetValue_AcceptsNonStringObject() {
        // setValue takes Object, not just String
        Attribute attr = new Attribute("count", "0");
        attr.setValue(Integer.valueOf(42));
        assertEquals(42, attr.getValue());
    }

    // ── Null & Exception Handling ─────────────────────────────────────

    @Test(expected = NullPointerException.class)
    public void testSetValue_NullThrowsNullPointerException() {
        // Objects.requireNonNull(value) is called inside setValue
        Attribute attr = new Attribute("key", "value");
        attr.setValue(null);   // must throw NullPointerException
    }

    // ── Object State ─────────────────────────────────────────────────

    @Test
    public void testIsManaged_AlwaysReturnsFalseForBaseClass() {
        Attribute attr = new Attribute("k", "v");
        assertFalse("Base Attribute.isManaged() must always be false",
                attr.isManaged());
    }

    @Test
    public void testToString_ContainsBothNameAndValue() {
        Attribute attr = new Attribute("env", "prod");
        String str = attr.toString();
        assertTrue("toString must contain the attribute name",
                str.contains("env"));
        assertTrue("toString must contain the attribute value",
                str.contains("prod"));
    }

    @Test
    public void testCopyConstructor_MutatingCopyDoesNotAffectOriginal() {
        Attribute original = new Attribute("x", "1");
        Attribute copy     = new Attribute(original);

        copy.setName("y");
        copy.setValue("2");

        // Original must be unchanged
        assertEquals("x", original.getName());
        assertEquals("1", original.getValue());
    }
}