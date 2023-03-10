package com.dadok.gaerval.global.util;

import java.time.Clock;

public interface TimeHolder {

	Clock getCurrentClock();

	long getMillis();
}
