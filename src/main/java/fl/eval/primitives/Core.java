package fl.eval.primitives;

import java.io.IOException;

import org.antlr.runtime.RecognitionException;

import fl.core.Function;
import fl.core.ILst;
import fl.core.IOrderedTupel;
import fl.core.IRecord;
import fl.core.ITupel;
import fl.core.Lists;
import fl.core.LstConstructor;
import fl.core.ToString;
import fl.core.TupelConstructor;
import fl.core.Tupels;
import fl.core.Unit;
import fl.eval.Env;
import fl.eval.Evaluator;
import fl.eval.Syntax;
import fl.frontend.Repl;
import fl.frontend.ast.Symbol;
import fl.frontend.ast2.AstConstructor;
import fl.frontend.ast2.IAstNode;
import fl.frontend.parser.Tree2Ast;
public class Core implements PrimitiveProvider{
	public Core(){}
	
	@Override
	public void install(Env env) {
		LstConstructor.install(env);
		TupelConstructor.install(env);
		
		AstConstructor.install(env);
		
	}
	
	
	@Primitive(name = "AND")
	public static boolean and(boolean a, boolean b) {
		return a && b; 
	}
	
	@Primitive(name = "OR")
	public static boolean or(boolean a, boolean b) {
		return a || b; 
	}
	
	
	@Primitive(name = "=")
	public static boolean eq(Object a, Object b) {
		if (a == null)
			return b == null;
		if (b == null)
			return a == null;
		return a.equals(b);
	}
	
	@Primitive(alias = "<>")
	public static Boolean ne(Object a, Object b) {
		return !eq(a,b);
	}
	
	@Primitive
	public static Boolean not(Boolean b){
		return Boolean.valueOf(!b.booleanValue());
	}


	@Primitive(name = "int")
	public static Integer toInt(Number arg) {
		return arg.intValue();
	}

	@Primitive(name = "double")
	public static Double toDouble(Number arg) {
		return arg.doubleValue();
	}

	@SuppressWarnings("rawtypes")
	@Primitive
	public static Object map(final Function f) {
		return new Function<ILst, Object>() {
			@Override
			public Object apply(ILst lst) {
				return lst.map(f);
			}
		};
	}
	
	@Primitive
	public static Object hd(ILst arg) {
		return arg.head();
	}
	

	@Primitive
	public static ILst tl(ILst arg) {
		return arg.tail();
	}
	
	@Primitive
	public static ILst formalsToList(Object arg){
		if(arg instanceof ITupel)
			return Lists.of(((ITupel)arg).array());
		return Lists.of(arg);
	}
	
	@Primitive
	public static Object listToformals(ILst arg){
		if(arg.isNil())
			return Unit.UNIT;
		
		Object[] elems = arg.array();
		if(elems.length==1)
			return elems[0];
		return Tupels.of(elems);
	}
	
	
	@Primitive(name="#")
	public static Object tupelAccess(final Object labelOrElement){
		return new Function<ITupel, Object>() {
			@Override
			public Object apply(ITupel tupel) {
				if(labelOrElement instanceof Number && tupel instanceof IOrderedTupel){
					return ((IOrderedTupel)tupel).at(((Number)labelOrElement).intValue() - 1);
				}
				else if(labelOrElement instanceof String && tupel instanceof IRecord){
					return ((IRecord)tupel).at(((String)labelOrElement));
				}
				
				throw new UnsupportedOperationException("Tupel-Access not implemented for this Tupel and label type: (" + tupel.getClass() + "," + labelOrElement.getClass() +")" );
			}
		};
	}
	


	@Primitive
	public static Object read(Object stringOrUnit) {
		final Object tree;
		try {
			if (Unit.UNIT.equals(stringOrUnit))
				tree = Tree2Ast.getParser(Repl.userRead(System.in,System.out)).repl()
						.getTree();
			else
				tree = Tree2Ast.getParser(ToString.asString(stringOrUnit)).mainexpEOF()
						.getTree();
			// System.out.println(((CommonTree)tree).toStringTree());
			return Tree2Ast.getAst(tree);
		} catch (IOException | RecognitionException e) {
			throw new RuntimeException(e);
		}
	}

//	@Primitive(name="assert")
//	public static void _assert(Object bb){
//		if(!Boolean.TRUE.equals(bb))
//			throw new AssertionError();
//	}
	
  @SuppressWarnings("serial")
  public static class AssertFailure extends RuntimeException {

    public AssertFailure(String string) {
      super(string);
    }
  }
	
	@Primitive(name="assert",syntax=true)
	public static void _assert(Env env,IAstNode ast){
	   if(!Boolean.TRUE.equals(new Evaluator().visit(ast, env))){
	       throw new AssertFailure("Assertion failed for expression " + ast.toString());
	   }
	}

	@Primitive(name="print")
	public static void println(Env env,Object arg){
		System.out.println(arg);
	}
	

	@Primitive(name="printenv",syntax=true)
	public static void printenv(Env env){
		env.dump(System.out);
	}

	@Primitive(name="quote",syntax=true)
	public static Object quote(Env env,IAstNode ast){
		return ast;
	}
	
	@Primitive(name="_funner",syntax=true)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object fun(final Env env,Object ast){
		final Evaluator e = new Evaluator();
		Object ret = e.visitApp(
		new Syntax() {
			@Override
			public Object apply(Env env, Object x) {
				return ((Function) e.visitId(new Symbol("_fun"), env))
						.apply(x);
			}
		}, ast, null, env);
		return eval(env, (IAstNode) ret);
	}
	
	@Primitive(name="eval")
	public static Object eval(Env env,IAstNode ast){
		return new Evaluator().visit(ast, env);
	}
	
	
	
	
}
