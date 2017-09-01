package fl.frontend.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

import fl.core.Function;
import fl.core.ILst;
import fl.core.IOrderedTupel;
import fl.core.Lists;
import fl.core.Records;
import fl.core.Tupels;
import fl.core.Unit;
import fl.frontend.ast.AstType;
import fl.frontend.ast.AstVisitor;
import fl.frontend.ast.Symbol;
import fl.frontend.ast2.AstConstructor;

public class Tree2Ast extends TreeVisitor {
	
	TTree2TAst ttvisitor=new TTree2TAst();
	
	@Override
	protected Object visitNil() {
		return Lists.nil();
	}
	
	@Override
	protected Object visitTupel(CommonTree t) {
		if(t.getChildCount()==0)
			return Unit.UNIT;
		return Tupels.of(visitChilds(t));
	}

	@Override
	protected Object visitList(CommonTree t) {
		return Lists.of(visitChilds(t));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object visitRecord(CommonTree t) {
		Map<String,Object> map=new HashMap<String,Object>();
		if(t.getChildCount()>0){
			for(CommonTree e : (Collection<CommonTree>)t.getChildren()){
				String key=e.getChild(0).getText();
				Object val=visit(e.getChild(1));
				map.put(key, val);
			}
		}
		return Records.recordOf(map);
	}
	
	
	@Override
	protected Object visitSeq(CommonTree t) {
		return Lists.cons(AstType.SEQ, Lists.of(visitChilds(t)));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object visitFn(CommonTree t) {
		return AstVisitor.newFn(Lists.of(t.getChildren()).map(
				new Function<CommonTree, IOrderedTupel>() {
					@Override
					public IOrderedTupel apply(CommonTree t) {
						return AstVisitor.newRule(visit(t.getChild(0)), visit(t.getChild(1)));
//								return Tupels.of(visitChilds(t));
					}
				}));
				
//				Tupels.of(AstType.FN, Lists.of(t.getChildren()).map(
//				new Function<CommonTree, IOrderedTupel>() {
//					@Override
//					public IOrderedTupel apply(CommonTree t) {
//						return Tupels.of(visitChilds(t));
//					}
//				}));
	}
	
	
	@Override
	protected Object visitFun(Object fn) {
		return AstVisitor.newApp(visitId("_funner"), fn);
//		return fn;
	}
	
	
	
	@Override
	protected Object visitLet(CommonTree bindings, Object exp) {
		return Tupels.of(AstType.LET,visit(bindings),visit(exp));
	}

	@Override
	protected Object visitApp(CommonTree t) {
		return AstVisitor.newApp(visit(t.getChild(0)), visit(t.getChild(1)));
	}
	
	@Override
	protected Object visitDot(CommonTree rcvr, String method) {
		return AstVisitor.newApp(new Symbol("."),Tupels.of(visit(rcvr),method));
	}
	
	@Override
	protected Object visitClazz(String text) {
		return AstVisitor.newApp(new Symbol("class"),visitString(text));
	}
	
	@Override
	protected Object visitVal(Object formals,Object val, boolean rec) {
		return Tupels.of(rec ? AstType.REC : AstType.VAL, visit(formals),
				visit(val));
	}
	
	@Override
	protected Object visitType(String id, Object tval) {
		return Tupels.of(AstType.TYPE, id,
				ttvisitor.visit(tval));
	}

	@Override
	protected Object visitIf(CommonTree t) {
		return AstVisitor.newIf(visit(t.getChild(0)), visit(t.getChild(1)),
				visit(t.getChild(2)));
	}

	@Override
	protected Object visitInteger(Number i) {
		return AstConstructor.NUM.apply(i);
	}

	@Override
	protected Object visitFloat(Number f) {
		return AstConstructor.NUM.apply(f);
	}

	@Override
	protected Object visitId(String text) {
//		return new Symbol(text);
		return AstConstructor.SYMBOL.apply(text);
	}

	@Override
	protected Object visitPrim(String primFunc) {
		return new Symbol(primFunc);
	}

	@Override
	protected Object visitString(String txt) {
		return AstConstructor.STRING.apply(txt);
	}
	
	@Override
	protected Object visitBoolean(Boolean bool) {
		return AstConstructor.BOOL.apply(bool);
	}
	
	@Override
	protected Object visitRecordAccess(String label) {
		return AstVisitor.newApp(visitId("#"), visitString(label));
	}
	
	@Override
	protected Object visitRecordAccess(Integer label) {
		return AstVisitor.newApp(visitId("#"), visitInteger(label));
	}
	
	@Override
	protected Object visitDataType(CommonTree t) {
		String name = t.getChild(0).getText();
		ILst typecons=Lists.nil();
		for(int i=1;i<t.getChildCount();i++){
			CommonTree c =(CommonTree) t.getChild(i);
			Object tt = visitTypeCons(c);
			typecons = Lists.append(tt, typecons);
		}
		return Tupels.of(AstType.DATATYPE,name,typecons);
	}
	
	private Object visitTypeCons(CommonTree c) {
		switch(c.getToken().getType()){
		case FLLexer.ID:
			return c.getText();
		case FLLexer.OF:
			return Tupels.of(c.getChild(0).getText(),ttvisitor.visit(c.getChild(1)));
		}
		return null;
	}

	@Override
	protected Object visitTypeAssertion(Tree exp, Tree texp) {
		return AstVisitor.newTa(visit(exp), new TTree2TAst().visit(texp));
	}

	public static FLParser getParser(String input) throws IOException {
		return getParser(new StringReader(input));
	}
	
	public static FLParser getParser(InputStream input) throws IOException {
		return getParser(new InputStreamReader(input));
	}
	
	public static FLParser getParser(Reader reader) throws IOException {
		FLLexer lexer = getLexer(reader);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		FLParser parser = new FLParser(tokens);
		return parser;
	}

	public static FLLexer getLexer(String input) throws IOException {
		return getLexer(new StringReader(input));
	}
	
	public static FLLexer getLexer(Reader reader) throws IOException {
		FLLexer lexer = new FLLexer(new ANTLRReaderStream(reader));
		return lexer;
	}

	public static Object getAst(Object tree){
		return new Tree2Ast().visit(tree);
	}
	
}
