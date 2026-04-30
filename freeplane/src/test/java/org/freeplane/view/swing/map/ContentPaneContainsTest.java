package org.freeplane.view.swing.map;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Panel;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class ContentPaneContainsTest {
	@Rule
	public final TestWatcher testWatcher = new TestWatcher() {
		@Override
		protected void succeeded(final Description description) {
			System.out.println(description.getMethodName() + " - test passed");
		}
	};

	@Test
	public void testPath1_SuperContains_ReturnsTrue() {
		final ContentPane pane = new ContentPane();
		pane.setBounds(0, 0, 100, 100);

		assertThat(pane.contains(10, 10)).isTrue();
	}

	@Test
	public void testPath2_EmptyComponents_ReturnsFalse() {
		final ContentPane pane = new ContentPane();
		pane.setBounds(0, 0, 100, 100);

		assertThat(pane.contains(150, 150)).isFalse();
	}

	@Test
	public void testPath3_ComponentMatch_ReturnsTrue() {
		final ContentPane pane = new ContentPane();
		pane.setBounds(0, 0, 100, 100);
		final Panel child = new Panel();
		child.setBounds(120, 80, 30, 30);
		child.setVisible(true);
		pane.add(child);

		assertThat(pane.contains(125, 85)).isTrue();
	}

	@Test
	public void testPath4_NoComponentMatch_ReturnsFalse() {
		final ContentPane pane = new ContentPane();
		pane.setBounds(0, 0, 100, 100);
		final Panel child = new Panel();
		child.setBounds(120, 80, 30, 30);
		child.setVisible(false);
		pane.add(child);

		assertThat(pane.contains(125, 85)).isFalse();
	}
}