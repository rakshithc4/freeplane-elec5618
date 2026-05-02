package org.freeplane.features.icon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.freeplane.core.util.LineComparator;
import org.junit.Test;

public class TagFilterIntegrationTest {

    @Test
    public void phase1_lineComparator_naturalSortBasic() {
        assertTrue(LineComparator.compareLinesParsingNumbers("file2", "file10") < 0);
    }

    @Test
    public void phase1_lineComparator_naturalSortMixed() {
        assertTrue(LineComparator.compareLinesParsingNumbers("item2b", "item10b") < 0);
    }

    @Test
    public void phase1_lineComparator_caseSensitivity() {
        assertTrue(LineComparator.compareLinesParsingNumbers("ABC", "abc") < 0);
    }

    @Test
    public void phase2_tag_compareTo_naturalOrder() {
        Tag tag2 = new Tag("file2", Color.RED);
        Tag tag10 = new Tag("file10", Color.BLUE);
        assertTrue(tag2.compareTo(tag10) < 0);
    }

    @Test
    public void phase2_tag_compareTo_reverse() {
        Tag tag10 = new Tag("file10", Color.RED);
        Tag tag2 = new Tag("file2", Color.BLUE);
        assertTrue(tag10.compareTo(tag2) > 0);
    }

    @Test
    public void phase2_tag_compareTo_equal() {
        Tag tag1 = new Tag("test", Color.GREEN);
        Tag tag2 = new Tag("test", Color.RED);
        assertEquals(0, tag1.compareTo(tag2));
    }

    @Test
    public void phase2_tag_compareTo_caseSensitive() {
        Tag tagUpper = new Tag("ABC", Color.BLUE);
        Tag tagLower = new Tag("abc", Color.RED);
        assertTrue(tagUpper.compareTo(tagLower) < 0);
    }

    @Test
    public void phase2_tag_sortingWithComparable() {
        List<Tag> tags = Arrays.asList(new Tag("item10"), new Tag("item1"), new Tag("item3"), new Tag("item2"));
        Collections.sort(tags);
        assertEquals("item1", tags.get(0).getContent());
        assertEquals("item2", tags.get(1).getContent());
        assertEquals("item3", tags.get(2).getContent());
        assertEquals("item10", tags.get(3).getContent());
    }

    @Test
    public void phase2_tag_copyPreservesComparison() {
        Tag original = new Tag("item5", Color.RED);
        Tag copy = original.copy();
        Tag other = new Tag("item3", Color.BLUE);
        assertEquals(original.compareTo(other), copy.compareTo(other));
    }

    @Test
    public void phase3_tagCompareCondition_equalsOperator() {
        TagCompareCondition condition = new TagCompareCondition("item5", false, 0, true, false, false, false);
        assertTrue(condition.isEqualityCondition());
    }

    @Test
    public void phase3_tagCompareCondition_checkTextEqual() {
        TagCompareCondition condition = new TagCompareCondition("item5", false, 0, true, false, false, false);
        assertTrue(condition.checkText("item5"));
    }

    @Test
    public void phase3_tagCompareCondition_checkTextNotEqual() {
        TagCompareCondition condition = new TagCompareCondition("item5", false, 0, true, false, false, false);
        assertTrue(!condition.checkText("item6"));
    }

    @Test
    public void phase3_tagCompareCondition_integratesWithTag() {
        Tag tag = new Tag("item10");
        TagCompareCondition condition = new TagCompareCondition("item10", false, 0, true, false, false, false);
        assertTrue(condition.checkText(tag.getContent()));
    }

    @Test
    public void phase3_tagCompareCondition_complexScenario() {
        List<Tag> tags = Arrays.asList(
            new Tag("chapter1"),
            new Tag("chapter2"),
            new Tag("chapter10"),
            new Tag("chapter20"),
            new Tag("chapter100")
        );
        Collections.sort(tags);
        assertEquals("chapter1", tags.get(0).getContent());
        assertEquals("chapter2", tags.get(1).getContent());
        assertEquals("chapter10", tags.get(2).getContent());
        assertEquals("chapter20", tags.get(3).getContent());
        assertEquals("chapter100", tags.get(4).getContent());
    }

    @Test
    public void phase3_integration_colorPropagation() {
        Tag tagA = new Tag("item5", Color.RED);
        Tag tagB = new Tag("item5", Color.BLUE);
        assertEquals(0, tagA.compareTo(tagB));
        assertEquals(Color.RED, tagA.getColor());
        assertEquals(Color.BLUE, tagB.getColor());
    }

    @Test
    public void phase3_integration_largeNumberHandling() {
        Tag small = new Tag("version999999999999");
        Tag large = new Tag("version1000000000000");
        assertTrue(small.compareTo(large) < 0);
    }
}
