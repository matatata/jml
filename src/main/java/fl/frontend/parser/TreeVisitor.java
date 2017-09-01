package fl.frontend.parser;

import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

public abstract class TreeVisitor {

	public TreeVisitor() {
	}

	public Object visit(Object exp) {
		CommonTree t = (CommonTree) exp;
		switch (t.getType()) {
		case FLLexer.TRUE:
			return visitBoolean(Boolean.TRUE);
		case FLLexer.FALSE:
			return visitBoolean(Boolean.FALSE);
		case FLLexer.INT:
			return visitInteger(parseInt(t.getText()));
		case FLLexer.FLOAT:
			return visitFloat(parseFloat(t.getText()));
		case FLLexer.ID:
			return visitId(t.getText());
		case FLLexer.SEQ:
			return visitSeq(t);
		case FLLexer.STRING:
			return visitString(t.getText());
		case FLLexer.TUPEL:
			return visitTupel(t);
		case FLLexer.LIST:
			return visitList(t);
		case FLLexer.RECORD:
			return visitRecord(t);
		case FLLexer.APP:
			return visitApp(t);
		case FLLexer.VAL:
			return visitVal(t.getChild(0),t.getChild(1), false);
		case FLLexer.TYPE:
			return visitType(t.getChild(0).getText(),t.getChild(1));
		case FLLexer.REC:
			return visitVal(t.getChild(0),t.getChild(1), true);
		case FLLexer.IF:
			return visitIf(t);
		case FLLexer.FN:
			return visitFn(t);
		case FLLexer.ADD:
		case FLLexer.MINUS:
		case FLLexer.MULT:
		case FLLexer.DIV:
		case FLLexer.RADD:
		case FLLexer.RMINUS:
		case FLLexer.RMULT:
		case FLLexer.RDIV:
		case FLLexer.NEQ:
		case FLLexer.EQ:
		case FLLexer.TILDE:
		case FLLexer.RTILDE:
		case FLLexer.GT:
		case FLLexer.GTE:
		case FLLexer.LT:
		case FLLexer.LTE:
		case FLLexer.AND:
		case FLLexer.OR:
			return visitPrim(t.getText());
		case FLLexer.COMPOSE:
			return visitPrim("_compose");
		case FLLexer.CONS:
			return visitPrim("cons");
		case FLLexer.COLON:
			return visitTypeAssertion(t.getChild(0),t.getChild(1));
		case FLLexer.LET:
			return visitLet((CommonTree) t.getChild(0),t.getChild(1));
		case FLLexer.NIL:
			return visitNil();
		case FLLexer.RECORDACC:
			return _visitRecordAccess(t.getText());
		case FLLexer.DATATYPE:
			return visitDataType(t);
		//experimental
		case FLLexer.DOT:
			return visitDot((CommonTree)t.getChild(0),t.getChild(1).getText());
		case FLLexer.CLAZZ:
			return visitClazz(t.getText());
		case FLLexer.FUN:
			return visitFun(visitFn(t));
		}
		throw new IllegalArgumentException("unexpected " + t.toStringTree());
	}

	protected abstract Object visitRecord(CommonTree t);

	private Object _visitRecordAccess(String tree) {
		Object label;
		try {
			return visitRecordAccess(Integer.valueOf(tree));
		} catch(NumberFormatException e){
			return visitRecordAccess(tree);
		}
		
//		Object l = visit(tree);
		
//		switch (tree.getType()) {
//		case FLLexer.STRING:
//			
//			break;
//
//		default:
//			break;
//		}
//		
//		return visitRecordAccess(l);
	}

	protected abstract Object visitRecordAccess(Integer label);


	protected abstract Object visitClazz(String text);

	protected abstract Object visitDot(CommonTree rcvr, String method);

	/**
	 * @param t (name typeconst+)
	 * @return
	 */
	protected abstract Object visitDataType(CommonTree t);

	protected abstract Object visitNil();

	protected abstract Object visitLet(CommonTree bindings, Object exp);

	protected abstract Object visitTypeAssertion(Tree exp, Tree texp);

	protected abstract Object visitVal(Object formals,Object val, boolean rec);

	protected abstract Object visitType(String id,Object tval);

	protected abstract Object visitPrim(String primFunc);

	protected abstract Object visitInteger(Number i);

	protected abstract Object visitFloat(Number f);
	
	protected abstract Object visitBoolean(Boolean bool);

	protected abstract Object visitId(String text);

	protected abstract Object visitString(String txt);

	protected abstract Object visitTupel(CommonTree t);

	protected abstract Object visitApp(CommonTree t);

	protected abstract Object visitList(CommonTree t);

	protected abstract Object visitSeq(CommonTree t);

	protected abstract Object visitFn(CommonTree t);
	
	/**
	 * syntactic sugar 
	 * fun name f1 f2 f_n = exp
	 * 
	 * <=>
	 * 
	 * val name=fn f1 => fn f2=> ... => fn f_n => exp 
	 * 
	 * @return
	 */
	protected abstract Object visitFun(Object fn);


	protected abstract Object visitIf(CommonTree t);

	protected Number parseInt(String txt) {
		return Integer.parseInt(txt);
	}

	protected Number parseFloat(String txt) {
		return Double.parseDouble(txt);
	}

	protected Object[] visitChilds(CommonTree t) {
		Object[] childs = new Object[t.getChildCount()];
		for (int i = 0; i < childs.length; i++) {
			childs[i] = visit(t.getChild(i));
		}
		return childs;
	}

	protected abstract Object visitRecordAccess(String label);
}
