package fl.frontend.tast;

import fl.frontend.ast.Symbol;


public class TypeSymbol extends Symbol {
	
	private Class<?> cl;

	public TypeSymbol(Class<?> cl) {
		super(cl.getName());
		this.cl = cl;
	}
	
	public TypeSymbol(String name) {
		super(name);
		this.cl=Object.class;
	}
	
	public boolean isInstance(Object thing){
		return cl.isInstance(thing);
	}
}
