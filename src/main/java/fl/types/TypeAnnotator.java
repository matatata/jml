package fl.types;

import fl.eval.Env;
import fl.frontend.ast.AstVisitorAdapter;
public class TypeAnnotator extends AstVisitorAdapter<Object, Env> {

	public long tvarCount = 0;

	private String newTVar() {
		return "'" + tvarCount++;
	}
	
	

}
