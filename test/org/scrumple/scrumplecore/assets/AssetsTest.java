package org.scrumple.scrumplecore.assets;

import java.nio.file.Paths;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class AssetsTest {

	@Test
	public void shouldInitFromExecutionDirectory() {
		Assets.init();
	}
	@Test
	public void shouldInitFromCustomDirectory() {
		Assets.init(Paths.get("hi"));
	}
}
