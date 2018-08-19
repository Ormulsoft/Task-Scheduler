package util;

public class StaticUtils {

	public static long getRemainingMemory() {
		long allocatedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
		return Runtime.getRuntime().maxMemory() - allocatedMemory;
	}
	
	public double getUsedMemory(){
		return ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024.0 * 1024.0 * 1024.0 ));		
	}
}
