package fl.core;

import fl.eval.types.ITypeConstructor;
import fl.frontend.ast2.IAstNode;

public class Unit implements IAstNode {
	public static Unit UNIT=new Unit();
	
	private Unit(){}
	
	@Override
	public String toString() {
		return "()";
	}

	@Override
	public ITypeConstructor getConstructor() {
		return TupelConstructor.tupel;
	}

	@Override
	public Object getData() {
		return this;
	}
}
