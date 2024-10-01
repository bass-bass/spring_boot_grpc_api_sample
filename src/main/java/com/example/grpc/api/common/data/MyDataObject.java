package com.example.grpc.api.common.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MyDataObject extends ConcurrentHashMap<String, String> {
	public void add(String key, String value) {
		putIfAbsent(key, value);
	}

	public int getCount() {
		return size();
	}

	public List<String> getAll() {
		List<String> values = new ArrayList<>(values());
		Collections.sort(values);
		return values;
	}
}
