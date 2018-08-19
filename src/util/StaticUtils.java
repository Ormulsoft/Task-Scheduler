package util;

/**
 * A class for various useful but miscellaneous static utility methods
 * @author Various, OrmulSoft
 *
 */
public class StaticUtils {
	
	/**
	 * Gets the current remaining memory of the runtime
	 * @return Remaining memory in MB
	 */
	public static long getRemainingMemory() {
		long allocatedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
		return Runtime.getRuntime().maxMemory() - allocatedMemory;
	}
	
	/**
	 * Gets the current used memory of the runtime
	 * @return Used memory in MB
	 */
	public static double getUsedMemory(){
		return ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024.0 * 1024.0 ));
		
	}
}
