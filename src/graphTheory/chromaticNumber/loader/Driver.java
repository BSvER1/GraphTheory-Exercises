package graphTheory.chromaticNumber.loader;

public class Driver {
	
	final static boolean TRACING = true;
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		trace(Driver.class, "Graph theory algorithm comparitor started.");
		
		new Control();
	}
	
	public static void trace(Class c, String msg) {
		if (TRACING) {
			System.out.println("[" + c.getSimpleName() + "]: " + msg);
		}
	}

}
