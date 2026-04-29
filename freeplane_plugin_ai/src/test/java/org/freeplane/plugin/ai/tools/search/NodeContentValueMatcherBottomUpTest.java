package org.freeplane.plugin.ai.tools.search;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import org.freeplane.features.map.MapModel;
import org.freeplane.features.map.NodeModel;
import org.freeplane.features.text.TextController;
import org.freeplane.plugin.ai.maps.AvailableMaps;
import org.freeplane.plugin.ai.tools.content.AttributesContentReader;
import org.freeplane.plugin.ai.tools.content.EditableContentReader;
import org.freeplane.plugin.ai.tools.content.IconsContentReader;
import org.freeplane.plugin.ai.tools.content.NodeContentItemReader;
import org.freeplane.plugin.ai.tools.content.NodeContentPreset;
import org.freeplane.plugin.ai.tools.content.NodeContentReader;
import org.freeplane.plugin.ai.tools.content.NodeContentRequest;
import org.freeplane.plugin.ai.tools.content.NodeContentResponse;
import org.freeplane.plugin.ai.tools.content.NodeContentValueMatcher;
import org.freeplane.plugin.ai.tools.content.NodeStyleContentReader;
import org.freeplane.plugin.ai.tools.content.TagsContentReader;
import org.freeplane.plugin.ai.tools.content.TextualContentReader;
import org.freeplane.plugin.ai.tools.content.TextualContentRequest;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class NodeContentValueMatcherBottomUpTest {
    @Test
    public void nodeContentReader_matchesUsesMatcherAndShortCircuitsOnTextualMatch() {
        TextualContentReader textualContentReader = mock(TextualContentReader.class);
        AttributesContentReader attributesContentReader = mock(AttributesContentReader.class);
        TagsContentReader tagsContentReader = mock(TagsContentReader.class);
        IconsContentReader iconsContentReader = mock(IconsContentReader.class);
        NodeStyleContentReader nodeStyleContentReader = mock(NodeStyleContentReader.class);
        EditableContentReader editableContentReader = mock(EditableContentReader.class);
        NodeContentReader nodeContentReader = new NodeContentReader(
            textualContentReader, attributesContentReader, tagsContentReader, iconsContentReader,
            nodeStyleContentReader, editableContentReader);
        NodeModel nodeModel = mock(NodeModel.class);
        TextualContentRequest textualContentRequest = new TextualContentRequest(true, false, false);
        NodeContentRequest request = new NodeContentRequest(textualContentRequest, null, null, null, null);
        NodeContentValueMatcher matcher = new NodeContentValueMatcher(
            "priority", SearchMatchingMode.CONTAINS, SearchCaseSensitivity.CASE_INSENSITIVE, null);
        when(textualContentReader.matches(eq(nodeModel), eq(textualContentRequest), eq(matcher)))
            .thenAnswer(invocation -> matcher.matchesValue("Priority 1"));

        boolean matched = nodeContentReader.matches(nodeModel, request, matcher);

        assertThat(matched).isTrue();
        verify(textualContentReader).matches(nodeModel, textualContentRequest, matcher);
        verifyNoInteractions(attributesContentReader, tagsContentReader, iconsContentReader);
    }

    @Test
    public void searchNodes_usesRegularExpressionMatcherForSearchResults() throws Exception {
        AvailableMaps availableMaps = mock(AvailableMaps.class);
        NodeContentItemReader nodeContentItemReader = mock(NodeContentItemReader.class);
        ObjectMapper objectMapper = mock(ObjectMapper.class);
        when(objectMapper.writeValueAsBytes(any())).thenReturn(new byte[100]);
        UUID mapIdentifier = UUID.fromString("f55a56bb-9aed-419d-af69-3dbb4982a420");
        MapModel mapModel = mock(MapModel.class);
        NodeModel rootNode = mock(NodeModel.class);
        NodeModel matchingNode = mock(NodeModel.class);
        NodeModel nonMatchingNode = mock(NodeModel.class);
        when(availableMaps.findMapModel(eq(mapIdentifier), any())).thenReturn(mapModel);
        when(mapModel.getRootNode()).thenReturn(rootNode);
        when(rootNode.getChildren()).thenReturn(Arrays.asList(matchingNode, nonMatchingNode));
        when(matchingNode.getChildren()).thenReturn(Collections.emptyList());
        when(nonMatchingNode.getChildren()).thenReturn(Collections.emptyList());
        when(matchingNode.createID()).thenReturn("ID_match");
        when(nodeContentItemReader.matchesNodeContent(eq(rootNode), any(NodeContentRequest.class),
            any(NodeContentValueMatcher.class)))
            .thenReturn(false);
        when(nodeContentItemReader.matchesNodeContent(eq(matchingNode), any(NodeContentRequest.class),
            any(NodeContentValueMatcher.class)))
            .thenAnswer(invocation -> {
                NodeContentValueMatcher matcher = invocation.getArgument(2);
                return matcher.matchesValue("Task 123");
            });
        when(nodeContentItemReader.matchesNodeContent(eq(nonMatchingNode), any(NodeContentRequest.class),
            any(NodeContentValueMatcher.class)))
            .thenAnswer(invocation -> {
                NodeContentValueMatcher matcher = invocation.getArgument(2);
                return matcher.matchesValue("Task ABC");
            });
        when(nodeContentItemReader.readNodeContent(matchingNode, null, NodeContentPreset.BRIEF))
            .thenReturn(new NodeContentResponse("Task 123", null, null, null, null, null, null, null));
        TextController textController = mock(TextController.class);
        SearchNodesTool uut = new SearchNodesTool(availableMaps, null, nodeContentItemReader, textController,
            objectMapper);
        SearchNodesRequest request = new SearchNodesRequest(
            mapIdentifier.toString(),
            "Task \\d+",
            null,
            null,
            SearchMatchingMode.REGULAR_EXPRESSION,
            SearchCaseSensitivity.CASE_SENSITIVE,
            Collections.emptyList(),
            0,
            200,
            1000);

        SearchNodesResponse response = uut.searchNodes(request);

        assertThat(response.getResults()).hasSize(1);
        assertThat(response.getResults().get(0).getNodeIdentifier()).isEqualTo("ID_match");
        assertThat(response.getResults().get(0).getBriefText()).isEqualTo("Task 123");
    }
}
