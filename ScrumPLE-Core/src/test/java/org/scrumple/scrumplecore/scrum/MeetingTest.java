package org.scrumple.scrumplecore.scrum;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MeetingTest {
	private static final String TYPE = "TEST";
	private static final long START = System.currentTimeMillis();
	private static final long END = START + 60 * 1000;

	private Meeting meeting = new Meeting(TYPE, START, END);

	@Test(expected = IllegalArgumentException.class)
	public void constructWithStartAfterEndIsIllegal() {
		meeting = new Meeting(TYPE, END, START);
	}
	@Test(expected = IllegalArgumentException.class)
	public void setTimeWithStartAfterEndIsIllegal() {
		meeting.setTime(END, START);
	}

	@Test
	public void clonedLengthEqualsSourceLength() {
		Meeting clone = new Meeting(meeting, END);

		assertEquals(meeting.getLength(), clone.getLength());
	}
}
