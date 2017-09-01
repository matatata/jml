package fl.frontend.ast;

import fl.core.ILst;
import fl.core.IOrderedTupel;
import fl.core.IRecord;

public class AstVisitorAdapter<R,C> extends AstVisitor<R,C> {

	@Override
	public R visitTan(Object exp, Object texp, Object original, C ctx) {
		return visit(exp,ctx);
	}
	
	@Override
	public R visitTypeDecl(String sym, Object val, C ctx) {
		return null;
	}
	@Override
	public R visitDataTypeDecl(String sym, ILst typeconsts, C ctx) {
		return null;
	}
	
	@Override
	public R visitSeq(ILst exps, C ctx) {
		R last=null;
		for(Object e : exps)
			last = visit(e,ctx);
		return last;
	}
	
	@Override
	public R visitRecord(IRecord exps, C ctx) {
		R last=null;
		for(Object e : exps)
			last = visit(e,ctx);
		return last;
	}
	
	@Override
	public R visitLet(ILst bindings, Object exp,C ctx) {
		visit(bindings,ctx);
		visit(exp,ctx);
		return null;
	}

	@Override
	public R visitIf(Object cond, Object then, Object elze, Object original,
			C ctx) {
		visit(cond,ctx);
		visit(then,ctx);
		visit(elze,ctx);
		return null;
	}

	@Override
	public R visitConst(Object f, C ctx) {
		return null;
	}

	@Override
	public R visitId(Symbol exp, C ctx) {
		return null;
	}

	@Override
	public R visitTupel(IOrderedTupel t, C ctx) {
		for(Object e : t){
			visit(e,ctx);
		}
		return null;
	}

	@Override
	public R visitList(ILst t, C ctx) {
		for(Object e : t){
			visit(e,ctx);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public R visitFn(ILst cases, Object original, C ctx) {
		for(IOrderedTupel c : (Iterable<IOrderedTupel>)cases){
			visit(c,ctx);
		}
		return null;
	}

	@Override
	public R visitApp(Object fun, Object arg, Object original, C ctx) {
		visit(fun,ctx);
		visit(arg,ctx);
		return null;
	}

	@Override
	public R visitVal(Object f, Object val, boolean rec, Object original,
			C ctx) {
		visit(val,ctx);
		return null;
	}

}
