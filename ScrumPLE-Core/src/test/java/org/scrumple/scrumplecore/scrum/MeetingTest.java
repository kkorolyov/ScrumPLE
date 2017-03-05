package org.scrumple.scrumplecore.scrum;

import static org.junit.Assert.assertEquals;

import java.time.Instant;

import org.junit.Test;

public class MeetingTest {
	private static final String TYPE = "TEST";
	private static final Instant START = Instant.now();
	private static final Instant END = START.plusSeconds(60);

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
