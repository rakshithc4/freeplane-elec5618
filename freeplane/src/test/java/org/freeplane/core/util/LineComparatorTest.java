package org.freeplane.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class LineComparatorTest {
	@Test
	public void testCompareLinesAlphabeticalOrder_ReturnsNegative() {
		assertTrue(LineComparator.compareLinesParsingNumbers("apple", "banana") < 0);
	}

	@Test
	public void testCompareLinesAlphabeticalOrder_ReturnsPositive() {
		assertTrue(LineComparator.compareLinesParsingNumbers("banana", "apple") > 0);
	}

	@Test
	public void testCompareLinesIdenticalStrings_ReturnsZero() {
		assertEquals(0, LineComparator.compareLinesParsingNumbers("chapter12", "chapter12"));
	}

	@Test
	public void testCompareNumericSegments_TreatsNumbersAsNumbers() {
		assertTrue(LineComparator.compareLinesParsingNumbers("item2", "item10") < 0);
	}

	@Test
	public void testCompareNumericSegments_LargerNumberGreater() {
		assertTrue(LineComparator.compareLinesParsingNumbers("item20", "item3") > 0);
	}

	@Test
	public void testCompareMixedTextAndNumbers_ParsesCorrectly() {
		assertTrue(LineComparator.compareLinesParsingNumbers("v2beta", "v10beta") < 0);
	}

	@Test
	public void testCompareMixedTextAndNumbers_SameNumberDifferentText() {
		assertTrue(LineComparator.compareLinesParsingNumbers("v2alpha", "v2beta") < 0);
	}

	@Test
	public void testCompareLargeNumbers_UsesNumericComparison() {
		assertTrue(LineComparator.compareLinesParsingNumbers("num999999999999", "num1000000000000") < 0);
	}

	@Test
	public void testCompareLargeNumbersWithPrefix() {
		assertTrue(LineComparator.compareLinesParsingNumbers("build1000000000000a", "build1000000000001a") < 0);
	}

	@Test
	public void testCompareLeadingZeros() {
		assertEquals(0, LineComparator.compareLinesParsingNumbers("img007", "img7"));
	}

	@Test
	public void testCompareZeroVsOne() {
		assertTrue(LineComparator.compareLinesParsingNumbers("x0", "x1") < 0);
	}

	@Test
	public void testCompareEmptyStrings_BothEmpty() {
		assertEquals(0, LineComparator.compareLinesParsingNumbers("", ""));
	}

	@Test
	public void testCompareEmptyString_EmptyVsNonEmpty() {
		assertTrue(LineComparator.compareLinesParsingNumbers("", "a") < 0);
	}

	@Test
	public void testCompareWhitespaceOnlyString_TrimmedBeforeComparison() {
		assertEquals(0, LineComparator.compareLinesParsingNumbers("   ", ""));
	}

	@Test
	public void testCompareCaseDifferences_UppercaseBeforeLowercase() {
		assertTrue(LineComparator.compareLinesParsingNumbers("ABC", "abc") < 0);
	}

	@Test
	public void testCompareLowercaseVsUppercase_SameLetter() {
		assertTrue(LineComparator.compareLinesParsingNumbers("a", "A") > 0);
	}

	@Test
	public void testCompareMixedCaseWords() {
		assertTrue(LineComparator.compareLinesParsingNumbers("Item2", "item2") < 0);
	}

	@Test
	public void testCompareCaseWithNumbers() {
		assertTrue(LineComparator.compareLinesParsingNumbers("A10", "a2") < 0);
	}

	@Test
	public void testCompareSpecialCharacters_ConsistentOrdering() {
		assertTrue(LineComparator.compareLinesParsingNumbers("a-1", "a_1") < 0);
	}

	@Test
	public void testCompareWithUnderscores() {
		assertTrue(LineComparator.compareLinesParsingNumbers("task_2", "task_10") < 0);
	}

	@Test
	public void testCompareNumericSegmentAtStart() {
		assertTrue(LineComparator.compareLinesParsingNumbers("2nd", "10th") < 0);
	}

	@Test
	public void testCompareDifferentLengths_ShorterPrefix() {
		assertTrue(LineComparator.compareLinesParsingNumbers("abc", "abc1") < 0);
	}

	@Test
	public void testCompareMultipleNumberSegments_FirstDecides() {
		assertTrue(LineComparator.compareLinesParsingNumbers("a2b9", "a10b1") < 0);
	}

	@Test
	public void testCompareMultipleNumberSegments_SecondDecides() {
		assertTrue(LineComparator.compareLinesParsingNumbers("a2b3", "a2b10") < 0);
	}

	@Test
	public void testComparisonSymmetry_ReverseArgumentsFlipsSign() {
		int ab = LineComparator.compareLinesParsingNumbers("file2", "file10");
		int ba = LineComparator.compareLinesParsingNumbers("file10", "file2");
		assertTrue(ab < 0);
		assertTrue(ba > 0);
	}

	@Test
	public void testTransitivity_ABlessC() {
		int ab = LineComparator.compareLinesParsingNumbers("x1", "x2");
		int bc = LineComparator.compareLinesParsingNumbers("x2", "x10");
		int ac = LineComparator.compareLinesParsingNumbers("x1", "x10");
		assertTrue(ab < 0 && bc < 0 && ac < 0);
	}

	@Test
	public void testRealWorldScenario_FileVersions() {
		assertTrue(LineComparator.compareLinesParsingNumbers("version2", "version10") < 0);
	}

	@Test
	public void testRealWorldScenario_ChapterNumbers() {
		assertTrue(LineComparator.compareLinesParsingNumbers("chapter9", "chapter10") < 0);
	}

	@Test
	public void testRealWorldScenario_ImageFileSequence() {
		assertTrue(LineComparator.compareLinesParsingNumbers("image12.png", "image100.png") < 0);
	}

	@Test
	public void testCompareNegativeNumberHandling() {
		int result = LineComparator.compareLinesParsingNumbers("item-2", "item-10");
		assertFalse(result == 0);
	}

	@Test
	public void testCompareMixedLargeSmalNumber() {
		assertTrue(LineComparator.compareLinesParsingNumbers("a999999", "a1000000") < 0);
	}
}
