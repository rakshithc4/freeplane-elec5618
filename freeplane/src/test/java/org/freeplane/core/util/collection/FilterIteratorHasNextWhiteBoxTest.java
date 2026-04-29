// file path:   ./freeplane/src/test/java/org/freeplane/core/util/collection/FilterIteratorHasNextWhiteBoxTest.java
package org.freeplane.core.util.collection;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

public class FilterIteratorHasNextWhiteBoxTest {
    @Test
    public void hasNext_path1_returnsTrueWhenCurrentAlreadyCached() {
        FilterIterator<String> iterator = new FilterIterator<>(
            Arrays.asList("hello").iterator(),
            s -> true
        );
        iterator.hasNext();
        assertTrue("Path 1 failed: expected true when hasCurrent already set",
                iterator.hasNext());
    }

    @Test
    public void hasNext_path2_returnsFalseWhenSourceEmpty() {
        FilterIterator<String> iterator = new FilterIterator<>(
            Collections.<String>emptyList().iterator(),
            s -> true
        );
        assertFalse("Path 2 failed: expected false for empty source",
                iterator.hasNext());
    }

    @Test
    public void hasNext_path3_returnsFalseWhenNoElementMatchesPredicate() {
        FilterIterator<String> iterator = new FilterIterator<>(
            Arrays.asList("A", "B", "C").iterator(),
            s -> false
        );
        assertFalse("Path 3 failed: expected false when predicate never matches",
                iterator.hasNext());
    }

    @Test
    public void hasNext_path4_returnsTrueWhenElementMatchesPredicate() {
        FilterIterator<String> iterator = new FilterIterator<>(
            Arrays.asList("match", "other").iterator(),
            s -> s.equals("match")
        );
        assertTrue("Path 4 failed: expected true when predicate matches an element",
                iterator.hasNext());
    }
}
