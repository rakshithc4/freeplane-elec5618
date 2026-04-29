package org.freeplane.plugin.ai.tools.content;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.regex.Pattern;

import org.freeplane.plugin.ai.tools.search.SearchCaseSensitivity;
import org.freeplane.plugin.ai.tools.search.SearchMatchingMode;
import org.junit.Test;

public class NodeContentValueMatcherTest {
    @Test
    public void constructor_rejectsNullMatchingMode() {
        assertThatThrownBy(() -> new NodeContentValueMatcher(
            "alpha", null, SearchCaseSensitivity.CASE_INSENSITIVE, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("matchingMode");
    }

    @Test
    public void constructor_rejectsNullCaseSensitivity() {
        assertThatThrownBy(() -> new NodeContentValueMatcher(
            "alpha", SearchMatchingMode.CONTAINS, null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("caseSensitivity");
    }

    @Test
    public void matchesValue_returnsFalseForNullValueOrQueryText() {
        NodeContentValueMatcher matcherWithQuery = new NodeContentValueMatcher(
            "alpha", SearchMatchingMode.CONTAINS, SearchCaseSensitivity.CASE_INSENSITIVE, null);
        NodeContentValueMatcher matcherWithoutQuery = new NodeContentValueMatcher(
            null, SearchMatchingMode.CONTAINS, SearchCaseSensitivity.CASE_INSENSITIVE, null);

        assertThat(matcherWithQuery.matchesValue(null)).isFalse();
        assertThat(matcherWithoutQuery.matchesValue("Alpha")).isFalse();
    }

    @Test
    public void matchesValue_containsHonorsCaseSensitivity() {
        NodeContentValueMatcher caseInsensitiveMatcher = new NodeContentValueMatcher(
            "alp", SearchMatchingMode.CONTAINS, SearchCaseSensitivity.CASE_INSENSITIVE, null);
        NodeContentValueMatcher caseSensitiveMatcher = new NodeContentValueMatcher(
            "alp", SearchMatchingMode.CONTAINS, SearchCaseSensitivity.CASE_SENSITIVE, null);

        assertThat(caseInsensitiveMatcher.matchesValue("Alpha")).isTrue();
        assertThat(caseSensitiveMatcher.matchesValue("Alpha")).isFalse();
    }

    @Test
    public void matchesValue_equalsHonorsCaseSensitivity() {
        NodeContentValueMatcher caseInsensitiveMatcher = new NodeContentValueMatcher(
            "alpha", SearchMatchingMode.EQUALS, SearchCaseSensitivity.CASE_INSENSITIVE, null);
        NodeContentValueMatcher caseSensitiveMatcher = new NodeContentValueMatcher(
            "alpha", SearchMatchingMode.EQUALS, SearchCaseSensitivity.CASE_SENSITIVE, null);

        assertThat(caseInsensitiveMatcher.matchesValue("Alpha")).isTrue();
        assertThat(caseSensitiveMatcher.matchesValue("Alpha")).isFalse();
    }

    @Test
    public void matchesValue_regularExpressionUsesProvidedPattern() {
        NodeContentValueMatcher matcher = new NodeContentValueMatcher(
            "alp.*", SearchMatchingMode.REGULAR_EXPRESSION, SearchCaseSensitivity.CASE_SENSITIVE,
            Pattern.compile("alp.*"));

        assertThat(matcher.matchesValue("alphabet")).isTrue();
        assertThat(matcher.matchesValue("beta")).isFalse();
    }

    @Test
    public void matchesValue_regularExpressionReturnsFalseWithoutPattern() {
        NodeContentValueMatcher matcher = new NodeContentValueMatcher(
            "alp.*", SearchMatchingMode.REGULAR_EXPRESSION, SearchCaseSensitivity.CASE_SENSITIVE, null);

        assertThat(matcher.matchesValue("alphabet")).isFalse();
    }
}
