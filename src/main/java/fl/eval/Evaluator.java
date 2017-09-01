package fl.eval;

import static fl.core.ToString.stringifyTypeExp;

import java.util.ArrayList;
import java.util.List;

import fl.core.EmptyListException;
import fl.core.Function;
import fl.core.IBinaTupel;
import fl.core.ILst;
import fl.core.IOrderedTupel;
import fl.core.IRecord;
import fl.core.ITupel;
import fl.core.Lists;
import fl.core.ToString;
import fl.core.Tupels;
import fl.core.Unit;
import fl.eval.primitives.Core;
import fl.eval.types.DataType;
import fl.eval.types.IDataTypeInstance;
import fl.eval.types.ITypeConstructor;
import fl.eval.types.TypeAbbreviation;
import fl.eval.types.TypeConstructor;
import fl.frontend.ast.AstVisitor;
import fl.frontend.ast.AstVisitorAdapter;
import fl.frontend.ast.Symbol;
import fl.frontend.ast2.IAstNode;
import fl.frontend.tast.Arrow;
import fl.frontend.tast.TypeSymbol;
import fl.frontend.tast.TypeVariable;
import fl.frontend.tast.TypeVisitorAdapter;
@SuppressWarnings({ "unchecked", "rawtypes" })
public class Evaluator extends AstVisitor<Object, Env> {


	@Override
	public Object visitConst(Object f, Env ctx) {
		return f;
	}

	@Override
	public Object visitId(Symbol symbol, Env ctx) {
		return ctx.lookup(symbol.image);
	}
	
	@Override
	public Object visitSeq(ILst exps, Env ctx) {
		if(exps==null)
			return null;
		Object last=null;
		for(Object e : exps)
			last = visit(e,ctx);
		return last;
	}
	
	@Override
	public Object visitLet(ILst bindings, Object exp, Env ctx) {
		Env env = new Env(ctx);
		for(Object binding : bindings)
			visit(binding,env);
		return visit(exp,env);
	}
	
	
	@Override
	public Object visitTupel(IOrderedTupel t, Env ctx) {
		Object[] result=new Object[t.length()];
		for (int i = 0; i < result.length; i++) {
			result[i]=visit(t.at(i),ctx);
		}
		return Tupels.of(result);
	}
	
	
	@Override
	protected Object visitRecord(IRecord t, final Env ctx) {
		return t.map(new Function<Object, Object>() {
			@Override
			public Object apply(Object obj) {
				return visit(obj,ctx);
			}
		});
	}

	@Override
	public Object visitList(ILst t, final Env ctx) {
		return t.map(new Function<Object, Object>() {
			@Override
			public Object apply(Object obj) {
				return visit(obj,ctx);
			}
		});
	}

	@Override
	public Object visitFn(ILst cases, Object original, final Env ctx) {
		if(cases.tail()==Lists.nil()){
			IOrderedTupel case1 =  cases.head();
			Object formals=case1.at(0);
			Object body=case1.at(1);
			return new Closure<Object,Object>(ctx, new FormalsPreprocessor().visit(formals, ctx), body);
		}
		
		ILst preprocessedCases = cases.map(new Function<IOrderedTupel,IOrderedTupel>(){
			@Override
			public IOrderedTupel apply(IOrderedTupel x) {
				return Tupels.of(new FormalsPreprocessor().visit(x.at(0),ctx), x.at(1));
			}
		});
		return new MatchingClosure<>(ctx, preprocessedCases);
	}
	
	@Override
	public Object visitApp(Object fun, Object arg, Object original, Env env) {
		Object f = visit(fun,env);
		if(f instanceof Syntax)
			return ((Syntax)f).apply(env,arg);
		else if (f instanceof Function)
			return ((Function)f).apply(visit(arg,env));
		else
			throw new MatchException("Not a function: " + fun);
	}
	
	@Override
	public Object visitVal(Object formals, Object val, boolean rec,
			Object original, Env ctx) {
//		Env env = rec ? new Env(ctx).define(sym, visit(val, ctx))
//				: ctx;
//		Object x = visit(val, env);
//		ctx.define(sym, x);
		//TODO rec!? Nothing to do, or what?
		
		
		bindArgs(new FormalsPreprocessor().visit(formals,ctx), visit(val, ctx), ctx);
		
		return Unit.UNIT;
	}
	
	
	
	
	
	public static class FormalsPreprocessor extends AstVisitorAdapter<Object, Env>{
		@Override
		public Object visitConst(Object f, Env ctx) {
			return f;
		}
		@Override
		public Object visitTan(Object exp, Object texp, Object original, Env ctx) {
			texp = new TypeEvaluator().visit(texp, ctx);
			return exp;
		}
		@Override
		public Object visitApp(Object fun, Object arg, Object original, Env ctx) {
			return AstVisitor.newApp(visit(fun,ctx),visit(arg,ctx));
//			return new AstConstructor.Application(Tupels.of(visit(fun,ctx),visit(arg,ctx)));
		}
		@Override
		public Object visitId(Symbol exp, Env ctx) {
			try{
				Object object = ctx.lookup(exp.image);
				if(object instanceof ITypeConstructor)
					return object; //gotcha
			}catch(NotBoundException e){
				//ok its a variable
			}
			return exp;
		}
		
		@Override
		public Object visitTupel(IOrderedTupel t, final Env ctx) {
			return t.map(new Function() {
				@Override
				public Object apply(Object x) {
					return visit(x,ctx);
				}
			});
		}
		
		
		@Override
		public Object visitList(ILst t, final Env ctx) {
			return t.map(new Function() {
				@Override
				public Object apply(Object x) {
					return visit(x,ctx);
				}
			});
		}
	}
	
	@Override
	public Object visitTypeDecl(String sym, Object val, Env ctx) {
		ctx.addTypeDef(sym, new TypeAbbreviation(sym, val));
		return Unit.UNIT;
	}
	
	
	@Override
	public Object visitDataTypeDecl(String sym, ILst typeconsts, final Env ctx) {
		final DataType dt = new DataType(sym);
		ctx.addTypeDef(sym, dt);
		ILst consst = typeconsts.map(new Function<Object, ITypeConstructor>() {
			@Override
			public ITypeConstructor apply(Object x) {
				if(x instanceof String)
					return new TypeConstructor.NullableTypeInstance(dt,(String)x);
				else
					return new TypeConstructor(dt,(String) Tupels.at(x, 0), new TypeEvaluator().visit(Tupels.at(x, 1),ctx));
			}
		});
		dt.setConstructors(consst);
		for(ITypeConstructor c :(Iterable<ITypeConstructor>) consst){
			ctx.define(c.getName(), c);
		}
		return Unit.UNIT;
	}
	
	@Override
	public Object visitIf(Object cond, Object then, Object elze,
			Object original, Env ctx) {
		return ((Boolean) visit(cond, ctx)) ? visit(then, ctx) : visit(
				elze, ctx);
	}
	
	
	private static class TypeEvaluator extends TypeVisitorAdapter<Object, Env>{
		private static List<String> importedJavaPackages = new ArrayList<>();
		
		static {
			importedJavaPackages.add("");
			importedJavaPackages.add("java.lang.");
		}
		
		@Override
		public Object visitTypeSymbol(TypeSymbol sym, Env ctx) {
			try{
				return ctx.lookupTypeDef(sym.image);
			}catch(NotBoundException e){
			}
			for(String pack : importedJavaPackages){
				try{
					return findAndDeclareJavaType(sym, ctx, pack);
				}catch(NotBoundException e){
				}
			}
			throw new NotBoundException(sym.image);
		}
		
		@Override
		public Object visitTypeVariable(TypeVariable typeVar, Env ctx) {
			return typeVar;
		}

		private Object findAndDeclareJavaType(Symbol sym, Env ctx, String pack) {
			try {
				Class<?> clazz = Class.forName(pack+sym.image);
				TypeSymbol tval = new TypeSymbol(clazz);
				ctx.addTypeDef(sym.image, tval);
				return tval;
			} catch (ClassNotFoundException e) {
			}
			throw new NotBoundException(sym.image);
		}
		
		@Override
		public Object visitArrow(Arrow exp, Env ctx) {
			return new Arrow(visit(exp.from,ctx),visit(exp.to,ctx));
		}
		
		@Override
		public Object visitProduct(ITupel exp, final Env ctx) {
			return exp.map(new Function() {
				@Override
				public Object apply(Object x) {
					return visit(x,ctx);
				}
			});
		}
		
		@Override
		protected Object visitUnit(Env ctx) {
			return Unit.UNIT;
		}
	}
	
	@Override
	public Object visitTan(Object exp, Object texp, Object original, Env ctx) {
		Object tt=new TypeEvaluator().visit(texp, ctx);
		System.out.println(stringifyTypeExp(tt));
		Object evaluated = visit(exp,ctx);
//		return AstVisitor.newTa(evaluated,texp);
		return evaluated;
	}
	

	public void bindArgs(Object formals, Object actualArg, final Env env) {
		bindArgs(formals, actualArg, new Function<IBinaTupel, Object>() {
			
			@Override
			public Object apply(IBinaTupel string_object) {
				env.define((String) string_object.first(), string_object.second());
				return Unit.UNIT;
			}
		});
	}

	/**
	 * 
	 * @param formals
	 * @param actualArg
	 * @param binder (String*Object)->()
	 */
	private void bindArgs(Object formals, Object actualArg, Function<IBinaTupel,Object> binder) {
		try {
			if (formals instanceof Symbol) {
				binder.apply(Tupels.of(((Symbol) formals).image, actualArg));
			}
			else if(AstVisitor.isApp(formals)){
				Object typeCons=Tupels.at(((IAstNode)formals).getData(), 0);
				if(!(typeCons instanceof ITypeConstructor))
					throw new IllegalArgumentException(typeCons + " is not a typeconstructor");
				Object args=Tupels.at(((IAstNode)formals).getData(), 1);
				
				if(!((ITypeConstructor)typeCons).isAssignableFrom(((IDataTypeInstance)actualArg).getConstructor()))
					throw MatchException.doesNotMatch(typeCons, ((IDataTypeInstance)actualArg).getConstructor());
				
				bindArgs(args, ((IDataTypeInstance)actualArg).getData(), binder);
			}
			else if (formals instanceof IOrderedTupel && !Lists.isNil(formals)) {
				IOrderedTupel tupel = (IOrderedTupel) formals;
				IOrderedTupel argTupel = (IOrderedTupel) actualArg;
				if(tupel.length()!=argTupel.length())
					throw MatchException.doesNotMatch(formals, actualArg,null);
				for (int i = 0; i < tupel.length(); i++) {
					bindArgs(tupel.at(i), argTupel.at(i), binder);
				}
			} else if(!Core.eq(formals,actualArg)){
				throw MatchException.doesNotMatch(formals, actualArg);
			}
		} catch(ClassCastException | ArrayIndexOutOfBoundsException | EmptyListException e ){
			throw MatchException.doesNotMatch(formals, actualArg,e);
		}
	}
	
	
}
