package fl.frontend.parser;

import org.antlr.runtime.tree.CommonTree;

public abstract class TTreeVisitor {

	public TTreeVisitor() {
	}

	public Object visit(Object exp) {
		CommonTree t = (CommonTree) exp;
		switch (t.getType()) {
		case FLLexer.TID:
			return visitTypeVariable(t.getText()); //Type variable 'a,'b ...
		case FLLexer.ID:
			return visitSymbol(t.getText()); //Konkrete Type name int,real,person
		case FLLexer.MULT:
		case FLLexer.TUPEL:
			return visitProduct(t);
		case FLLexer.TARROW:
			return visitTFn(t);
		case FLLexer.RECORD:
			return visitTRec(t);
		}
		throw new IllegalArgumentException("unexpected " + t.toStringTree());
	}


	protected abstract Object visitTRec(CommonTree t);

	protected abstract Object visitSymbol(String text);
	protected abstract Object visitTypeVariable(String text);
	protected abstract Object visitProduct(CommonTree t);
	protected abstract Object visitTFn(CommonTree t);


	protected Object[] visitChilds(CommonTree t) {
		Object[] childs = new Object[t.getChildCount()];
		for (int i = 0; i < childs.length; i++) {
			childs[i] = visit(t.getChild(i));
		}
		return childs;
	}
}
