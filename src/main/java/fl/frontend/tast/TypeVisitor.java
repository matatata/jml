package fl.frontend.tast;

import fl.core.ITupel;
import fl.core.Unit;
import fl.eval.types.DataType;
import fl.eval.types.TypeAbbreviation;

public abstract class TypeVisitor<R,C> implements ITypeVisitor<R, C> {
	@Override
	public R visit(Object exp,C ctx){
		if(exp instanceof TypeVariable){
			return visitTypeVariable((TypeVariable)exp,ctx);
		}
		if(exp instanceof TypeSymbol){
			return visitTypeSymbol((TypeSymbol)exp,ctx);
		}
		if(exp instanceof Arrow){
			return visitArrow((Arrow)exp,ctx);
		}
		if(exp instanceof ITupel){
			return visitProduct((ITupel)exp,ctx);
		}
		if(exp instanceof DataType)
			return visitDataType((DataType)exp,ctx);
		if(exp instanceof TypeAbbreviation)
			return visitTypeAbbreviation((TypeAbbreviation)exp,ctx);
		if(Unit.UNIT.equals(exp)){
			return visitUnit(ctx);
		}
			
		throw new IllegalArgumentException("unexpected: " + exp);
	}

	protected abstract R visitUnit(C ctx);

	protected abstract R visitTypeAbbreviation(TypeAbbreviation exp, C ctx);
	protected abstract R visitDataType(DataType exp, C ctx);

	/**
	 * konkrete int,bool, person, age
	 * @param sym
	 * @param ctx
	 * @return
	 */
	@Override
	public abstract R visitTypeSymbol(TypeSymbol sym, C ctx);
	
	/**
	 * 'a, 'b placeholder
	 * @param typeVar
	 * @param ctx
	 * @return
	 */
	@Override
	public abstract R visitTypeVariable(TypeVariable typeVar, C ctx);

	@Override
	public abstract R visitArrow(Arrow exp, C ctx);

	@Override
	public abstract R visitProduct(ITupel exp, C ctx);
}
