package com.dadok.gaerval.testutil;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.dadok.gaerval.global.util.TimeHolder;

public class TestTimeHolder implements TimeHolder {

	private Clock clock;

	public TestTimeHolder(Clock clock) {
		this.clock = clock;
	}

	@Override
	public Clock getCurrentClock() {
		return this.clock;
	}

	@Override
	public long getMillis() {
		return this.clock.millis();
	}

	//pattern : 2000-01-01T00:00:00.00Z
	public static Clock click(String timePattern) {
		return Clock.fixed(Instant.parse(timePattern), ZoneId.of("UTC"));
	}

	public static Clock localDateToClock(LocalDate localDate) {
		LocalDateTime localDateTime = localDate.atStartOfDay();
		ZoneId zoneId = ZoneId.systemDefault();
		Clock clock = Clock.system(zoneId);
		Instant instant = localDateTime.atZone(zoneId).toInstant();
		return Clock.fixed(instant, zoneId);
	}

	public static Clock localDateToClockStartOfDay(LocalDate localDate) {
		ZoneId zoneId = ZoneId.systemDefault();
		Instant instant = localDate.atStartOfDay(zoneId).toInstant();
		return Clock.fixed(instant, zoneId);
	}

	public static TestTimeHolder now() {
		return new TestTimeHolder(localDateToClockStartOfDay(LocalDate.now()));
	}

	public static TestTimeHolder of(LocalDate localDate) {
		return new TestTimeHolder(localDateToClockStartOfDay(localDate));
	}
}
