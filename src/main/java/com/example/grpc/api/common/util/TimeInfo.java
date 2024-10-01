package com.example.grpc.api.common.util;

import java.util.concurrent.TimeUnit;

import lombok.Getter;

public class TimeInfo {
	@Getter private int val;
	@Getter private TimeUnit unit;

	public TimeInfo(String val) {
		if (val == null)
			return;
		val = val.toLowerCase();

		boolean hasUnit = false;
		int pos = 0;
		for (; pos < val.length(); pos++) {
			char s = val.charAt(pos);
			if (!Character.isDigit(s)) {
				hasUnit = true;
				break;
			}
		}

		if (pos == 0)
			throw new IllegalArgumentException("number do not contain");
		String strVal = val.substring(0, pos);
		int val_ = Integer.parseInt(strVal);

		TimeUnit unit = null;
		if (hasUnit) {
			String strUnit = val.substring(pos);

			switch (strUnit) {
				case "d":
					unit = TimeUnit.DAYS;
					break;

				case "h":
					unit = TimeUnit.HOURS;
					break;

				case "m":
					unit = TimeUnit.MINUTES;
					break;

				case "s":
					unit = TimeUnit.SECONDS;
					break;

				case "ms":
					unit = TimeUnit.MILLISECONDS;
					break;
			}
		} else {
			unit = TimeUnit.MILLISECONDS;
		}

		this.val = val_;
		this.unit = unit;
	}
}
