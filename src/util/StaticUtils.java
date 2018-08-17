package util;

public class StaticUtils {

	public static long getRemainingMemory() {
		long allocatedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
		return Runtime.getRuntime().maxMemory() - allocatedMemory;
	}
}
