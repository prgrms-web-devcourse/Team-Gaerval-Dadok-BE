package com.dadok.gaerval.global.util;

import java.time.Clock;

import org.springframework.stereotype.Component;

@Component
public class StandardTimeHolder implements TimeHolder {

	@Override
	public Clock getCurrentClock() {
		return Clock.systemDefaultZone();
	}

	@Override
	public long getMillis() {
		return Clock.systemUTC().millis();
	}

}
