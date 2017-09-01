package fl.frontend.tast;

import fl.core.ITupel;
import fl.eval.types.DataType;
import fl.eval.types.TypeAbbreviation;

public class TypeVisitorAdapter<R,C> extends TypeVisitor<R,C> {

	@Override
	public R visitTypeSymbol(TypeSymbol sym, C ctx) {
		return null;
	}
	
	@Override
	protected R visitUnit(C ctx) {
		return null;
	}

	@Override
	public R visitTypeVariable(TypeVariable typeVar, C ctx) {
		return null;
	}

	@Override
	public R visitArrow(Arrow exp, C ctx) {
		visit(exp.from,ctx);
		visit(exp.to,ctx);
		return null;
	}

	@Override
	public R visitProduct(ITupel exp, C ctx) {
		for(Object e : exp)
			visit(e,ctx);
		return null;
	}
	
	@Override
	protected R visitDataType(DataType exp,C ctx) {
		return null;
	}
	
	@Override
	protected R visitTypeAbbreviation(TypeAbbreviation exp, C ctx) {
		return visit(exp.getType(),ctx);
	}	
	
}
