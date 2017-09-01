package fl.types;

import fl.core.ILst;
import fl.frontend.tast.Arrow;
import fl.frontend.tast.TypeSymbol;
import fl.frontend.tast.TypeVariable;


public abstract class Visitor<R,C> {

	public Visitor() {
	}

	
	public R visit(Object x,C ctx){
		if(x instanceof String)
			return visitTId((String)x,ctx);
		if(x instanceof Arrow)
			return visitFn((Arrow)x,ctx);
		if(x instanceof TypeVariable)
			return visitConst((TypeVariable)x,ctx);
		if(x instanceof TypeSymbol)
			return visitConst((TypeSymbol)x,ctx);
		if (x instanceof ILst)
			return visitPoduct((ILst) x, ctx);
		throw new IllegalArgumentException(String.valueOf(x));
	}
	
	protected Object[] visitAll(Object[] t,C ctx) {
		Object[] childs = new Object[t.length];
		for (int i = 0; i < childs.length; i++) {
			childs[i] = visit(t[i],ctx);
		}
		return childs;
	}

	protected abstract R visitTId(String x, C ctx);

	protected abstract R visitPoduct(ILst x, C ctx);
	protected abstract R visitConst(TypeVariable x, C ctx);
	protected abstract R visitConst(TypeSymbol x, C ctx);
	protected abstract R visitFn(Arrow x, C ctx);
}
