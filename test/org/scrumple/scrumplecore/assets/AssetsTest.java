package org.scrumple.scrumplecore.assets;

import org.junit.Test;

import dev.kkorolyov.simplelogs.Logger;

@SuppressWarnings("javadoc")
public class AssetsTest {
	@Test
	public void testInit() {
		Assets.init();	// Mainly to test file initialization
		Logger.getLogger(Assets.class.getName()).info("TestDebug");
	}
}
