package fl.frontend.ast;

import fl.core.ILst;
import fl.core.IOrderedTupel;

public interface IAstVisitor<R, C> {

	R visit(Object exp, C ctx);

	/**
	 * 
	 * @param exps can be nil
	 * @param ctx
	 * @return
	 */
	R visitSeq(ILst exps, C ctx);

	R visitLet(ILst bindings, Object exp, C ctx);

	R visitTan(Object exp, Object texp, Object original, C ctx);

	R visitIf(Object cond, Object then, Object elze, Object original, C ctx);

	R visitConst(Object f, C ctx);

	R visitId(Symbol exp, C ctx);

	R visitTupel(IOrderedTupel t, C ctx);

	R visitList(ILst t, C ctx);

	R visitFn(ILst cases, Object original, C ctx);

	R visitApp(Object fun, Object arg, Object original, C ctx);

	R visitVal(Object formals, Object val, boolean rec, Object original, C ctx);

	R visitTypeDecl(String sym, Object val, C ctx);

}