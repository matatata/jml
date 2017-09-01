package fl.frontend.tast;

import fl.core.ITupel;

public interface ITypeVisitor<R, C> {

	R visit(Object exp, C ctx);

	/**
	 * konkrete int,bool, person, age
	 * @param sym
	 * @param ctx
	 * @return
	 */
	R visitTypeSymbol(TypeSymbol sym, C ctx);

	/**
	 * 'a, 'b placeholder
	 * @param typeVar
	 * @param ctx
	 * @return
	 */
	R visitTypeVariable(TypeVariable typeVar, C ctx);

	R visitArrow(Arrow exp, C ctx);

	R visitProduct(ITupel exp, C ctx);

}