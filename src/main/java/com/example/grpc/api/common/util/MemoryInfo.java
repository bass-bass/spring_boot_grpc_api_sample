package com.example.grpc.api.common.util;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

public class MemoryInfo {

	enum Unit {
		NONE(0, "B"),
		KIRO(1, "KB"),
		MEGA(2, "MB"),
		GIGA(3, "GB");

		@Getter private final long value;

		@Getter private final String s;

		private Unit(int value, String name) {
			this.value = (long) (Math.pow(1024, value));
			this.s = name;
		}
	}

	private final long free;
	private final long total;
	private final long max;
	private final long used;

	private final int usedRate;

	private Unit unit = Unit.NONE;

	public MemoryInfo() {
		Runtime rt = Runtime.getRuntime();

		this.free = rt.freeMemory();
		this.total = rt.totalMemory();
		this.max = rt.maxMemory();

		long used = total - free;
		if (used < 0)
			used = 0;
		this.used = used;

		int usedRate = 0;
		if (max > 0)
			usedRate = (int) ((100 * used) / max);
		if (usedRate > 100)
			usedRate = 100;
		this.usedRate = usedRate;
	}

	public long getFree() {
		return free / unit.value;
	}

	public String getFreeStr() {
		return free / unit.value + unit.s;
	}

	public long getTotal() {
		return total / unit.value;
	}

	public String getTotalStr() {
		return total / unit.value + unit.s;
	}

	public long getMax() {
		return max / unit.value;
	}

	public String getMaxStr() {
		return max / unit.value + unit.s;
	}

	public long getUsed() {
		return used / unit.value;
	}

	public String getUsedStr() {
		return used / unit.value + unit.s;
	}

	public int getUsedRate() {
		return usedRate;
	}

	@Override
	public String toString() {
		return toMap().toString();
	}

	public MemoryInfo setKiro() {
		this.unit = Unit.KIRO;
		return this;
	}

	public MemoryInfo setMega() {
		this.unit = Unit.MEGA;
		return this;
	}

	public MemoryInfo setGiga() {
		this.unit = Unit.GIGA;
		return this;
	}

	public Map<String, String> toMap() {
		Map<String, String> map = new HashMap<>();
		map.put("total", getTotalStr());
		map.put("free", getFreeStr());
		map.put("max (-Xmx)", getMaxStr());
		map.put("used", getUsedStr());
		map.put("used-rate", String.valueOf(usedRate));
		return map;
	}
}