package graphTheory.hamiltonianCycles.loader;

import graphTheory.hamiltonianCycles.solver.SLH;

public class Driver {
	
	final static boolean TRACING = true;
	private static boolean managed = true;
	private static boolean runAllTests = true;
	
	
	public static void main(String[] args) {
	
		trace("Graph theory algorithm comparitor started.");
		
		new SLH(null);
		
		
//		if (managed)
//			new Control();
//		else
//			new Control(runAllTests);
		

	}
	
	
	/**
	 * Old method for printing debugging messages to console. Use <code> trace(String msg) </code> instead.
	 * @param c class that is calling the method.
	 * @param msg message to print.
	 */
	@SuppressWarnings("rawtypes")
	@Deprecated
	public static void trace(Class c, String msg) {
		if (TRACING) {
			System.out.println("[" + c.getSimpleName() + "]: " + msg);
		}
	}
	
	/**
	 * Print nicely formatted debug message to console.
	 * @param msg String message to print.
	 */
	public static void trace(String msg) {
		if (TRACING) {
			System.out.println("[" + Thread.currentThread().getStackTrace()[2].getClassName().substring(
					Thread.currentThread().getStackTrace()[2].getClassName().lastIndexOf('.')+1)+"."
					+ "" + Thread.currentThread().getStackTrace()[2].getMethodName() + "()]: " + msg);
		}
	}
	
	/**
	 * Print nicely formatted debug message with timestamp to console.
	 * @param time Time associated with message.
	 * @param msg Message to print.
	 */
	public static void trace(long time, String msg) {
		if (TRACING) {
			
			if (time > 3600000) {
				System.out.println("[" + Thread.currentThread().getStackTrace()[2].getClassName().substring(Thread.currentThread().getStackTrace()[2].getClassName().lastIndexOf('.')+1)+"."
						+ "" + Thread.currentThread().getStackTrace()[2].getMethodName() + "()] {"+1.0f*time/3600000+"h}: " + msg);
			} else if (time > 60000) {
				System.out.println("[" + Thread.currentThread().getStackTrace()[2].getClassName().substring(Thread.currentThread().getStackTrace()[2].getClassName().lastIndexOf('.')+1)+"."
						+ "" + Thread.currentThread().getStackTrace()[2].getMethodName() + "()] {"+1.0f*time/60000+"m}: " + msg);
			} else if (time > 1000) {
				System.out.println("[" + Thread.currentThread().getStackTrace()[2].getClassName().substring(Thread.currentThread().getStackTrace()[2].getClassName().lastIndexOf('.')+1)+"."
						+ "" + Thread.currentThread().getStackTrace()[2].getMethodName() + "()] {"+1.0f*time/1000+"s}: " + msg);
			} else {
				System.out.println("[" + Thread.currentThread().getStackTrace()[2].getClassName().substring(Thread.currentThread().getStackTrace()[2].getClassName().lastIndexOf('.')+1)+"."
						+ "" + Thread.currentThread().getStackTrace()[2].getMethodName() + "()] {"+time+"ms}: " + msg);
			}
			
		}
	}
}
