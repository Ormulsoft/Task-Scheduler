package util;

import java.util.LinkedHashMap;

public class BenchmarkSingleton {
/**
 * Benchmarking class to time events such as algorithm runs
 */
	
	LinkedHashMap<String, Long> timeList = new LinkedHashMap<String, Long>();	
	
	private static BenchmarkSingleton instance;
	long start;
	private BenchmarkSingleton() {}

	public static BenchmarkSingleton getInstance() {
		if(instance == null) {
			instance = new BenchmarkSingleton();
		}
		return instance;
	}
	
	public void logTime(String recordName) {
		timeList.put(recordName, System.currentTimeMillis());
	}
	
}
