package fl.frontend.ast;

import org.antlr.runtime.tree.CommonTree;

import fl.core.Function;
import fl.core.IBinaTupel;
import fl.core.ILst;
import fl.core.IOrderedTupel;
import fl.core.IRecord;
import fl.core.Lists;
import fl.core.Tupels;
import fl.eval.types.IDataTypeInstance;
import fl.frontend.ast2.AstConstructor;
import fl.frontend.ast2.IAstNode;
public abstract class AstVisitor<R, C> implements IAstVisitor<R, C> {


	@Override
	public final R visit(Object exp, C ctx) {
		if (AstVisitor.isTan(exp))
			return visitTan(Tupels.at(exp, 1), Tupels.at(exp, 2), exp, ctx);
		if (AstVisitor.isFn(exp))
			return visitFn((ILst) ((IDataTypeInstance)exp).getData(), exp, ctx);
		if (AstVisitor.isApp(exp)){
			return visitApp(Tupels.at(((IAstNode)exp).getData(),0), Tupels.at(((IAstNode)exp).getData(), 1), exp, ctx);
		}
		if(AstVisitor.isType(exp))
			return visitTypeDecl((String) Tupels.at(exp, 1), Tupels.at(exp, 2),ctx);
		if(AstVisitor.isDataType(exp))
			return visitDataTypeDecl((String) Tupels.at(exp, 1), (ILst) Tupels.at(exp, 2),ctx);
		boolean rec = false;
		if (AstVisitor.isVal(exp) || (rec = AstVisitor.isRec(exp)))
			return visitVal(Tupels.at(exp, 1), Tupels.at(exp, 2), rec, exp, ctx);
		
		if (AstVisitor.isIf(exp)){
			IDataTypeInstance node=(IDataTypeInstance) exp;
			Object ifThenElseTupel = node.getData();
			return visitIf(Tupels.at(ifThenElseTupel, 0), Tupels.at(ifThenElseTupel, 1),
					Tupels.at(ifThenElseTupel, 2), exp, ctx);
		}
		if(AstVisitor.isLet(exp))
			return visitLet((ILst) Tupels.at(exp,1),Tupels.at(exp,2),ctx);
		if(AstVisitor.isSeq(exp))
			return visitSeq(((ILst)exp).tail(),ctx);
		if (exp instanceof IRecord)
			return visitRecord((IRecord) exp, ctx);
		if (exp instanceof ILst)
			return visitList((ILst) exp, ctx);
		if (exp instanceof IOrderedTupel)
			return visitTupel((IOrderedTupel) exp, ctx);
		if (exp instanceof Symbol)
			return visitId((Symbol) exp, ctx);
		else if(exp instanceof IAstNode){
			return visitConst(((IAstNode)exp).getData(),ctx);
		}
		else
		{
			return visitConst(exp, ctx);
		}
	}

	protected abstract R visitRecord(IRecord exp, C ctx);

	/**
	 * 
	 * @param exps can be nil
	 * @param ctx
	 * @return
	 */
	@Override
	public abstract R visitSeq(ILst exps, C ctx);

	@Override
	public abstract R visitLet(ILst bindings, Object exp,C ctx);

	@Override
	public abstract R visitTan(Object exp, Object texp, Object original, C ctx);
	
	@Override
	public abstract R visitIf(Object cond, Object then, Object elze,
			Object original, C ctx);

	@Override
	public abstract R visitConst(Object f, C ctx);

	@Override
	public abstract R visitId(Symbol exp, C ctx);

	@Override
	public abstract R visitTupel(IOrderedTupel t, C ctx);

	@Override
	public abstract R visitList(ILst t, C ctx);

	@Override
	public abstract R visitFn(ILst cases, Object original,
			C ctx);

	@Override
	public abstract R visitApp(Object fun, Object arg, Object original, C ctx);

	@Override
	public abstract R visitVal(Object formals, Object val, boolean rec,
			Object original, C ctx);

	@Override
	public abstract R visitTypeDecl(String sym, Object val, C ctx);
	
	public abstract R visitDataTypeDecl(String sym, ILst typeconsts, C ctx);
	
	public static Object newApp(Object fun,Object arg){
		return new AstConstructor.Application(Tupels.of(fun,arg));
	}
	
	/**
	 * @param rules with rule is a {@link IBinaTupel} (formals,exp)
	 * @return
	 */
	public static Object newFn(ILst rules){
//		return Tupels.of(AstType.FN, rules);
		return new AstConstructor.AstNode(AstConstructor.FN,rules);
	}
	
	public static IBinaTupel newRule(Object formal,Object expr){
		return Tupels.of(formal, expr);
	}
	
	public static boolean isApp(Object exp) {
		return AstConstructor.APP.isInstance(exp);
	}

	public static Object newTa(Object exp, Object type) {
		return Tupels.of(AstType.TAN, exp, type);
	}

	public static boolean isFn(Object exp) {
//		return isTaggedTupel(AstType.FN, exp);
		return AstConstructor.FN.isInstance(exp);
	}

	public static boolean isLet(Object exp) {
		return isTaggedTupel(AstType.LET, exp);
	}
	
	
	public static boolean isSeq(Object l){
		return isTaggedList(AstType.SEQ, l);
	}
	

	public static boolean isIf(Object exp) {
		return AstConstructor.IF.isInstance(exp);
	}

	public static Object newIf(Object test, Object then, Object elze) {
		return AstConstructor.IF.apply(Tupels.of(test,then,elze));
	}
	
	public static boolean isType(Object exp) {
		return isTaggedTupel(AstType.TYPE, exp);
	}
	
	public static boolean isDataType(Object exp) {
		return isTaggedTupel(AstType.DATATYPE, exp);
	}

	public static boolean isVal(Object exp) {
		return isTaggedTupel(AstType.VAL, exp);
	}

	public static boolean isRec(Object exp) {
		return isTaggedTupel(AstType.REC, exp);
	}

	

	public static boolean isTan(Object exp) {
		return isTaggedTupel(AstType.TAN, exp);
	}

	public static boolean isTaggedList(AstType tag, Object exp) {
		return exp instanceof ILst && !((ILst)exp).isNil() && tag.equals(((ILst)exp).head());
	}
	
	public static boolean isTaggedTupel(AstType tag, Object exp) {
		return (exp instanceof IOrderedTupel) && ((IOrderedTupel) exp).length() > 0
				&& tag.equals(((IOrderedTupel) exp).at(0));
	}

}
