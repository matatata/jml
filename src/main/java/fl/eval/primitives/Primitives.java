package fl.eval.primitives;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import fl.core.Function;
import fl.core.ILst;
import fl.core.IOrderedTupel;
import fl.core.Unit;
import fl.eval.Env;
import fl.eval.MatchException;
import fl.eval.Syntax;

public class Primitives {

	
	
	
	public static class PrimitiveFunction implements Function<Object, Object> {
		private final MethodHandle methodHandle;
		private final String name;
		private final boolean varArgs;
	
		public PrimitiveFunction(MethodHandle methodHandle, String name,
				boolean varArgs) {
			this.name = name;
			this.varArgs = varArgs;
			if (methodHandle.type().parameterCount() > 1 && !varArgs) {
				throw new IllegalArgumentException(methodHandle.type()
						.parameterCount()
						+ " arguments, but varArgs="
						+ varArgs);
			}
			
			//no args, so drop whatever is given
			if (methodHandle.type().parameterCount()==0){
				methodHandle = MethodHandles.dropArguments(methodHandle, 0, Object.class);
			}
			
			methodHandle = wrapVoidRetWithUnitIfNecessary(methodHandle);
			this.methodHandle = methodHandle;
		}
	
		@Override
		public Object apply(Object x) {
			try {
				if (varArgs) {
					if (x instanceof IOrderedTupel)
						return methodHandle.invokeWithArguments(((IOrderedTupel) x)
								.array());
					//unnecessary!?
					else if (x instanceof ILst)
						return methodHandle.invokeWithArguments(((ILst) x)
								.array());
					else
						throw new ClassCastException(x
								+ " is no Tupel and no List.");
				} else {
					return methodHandle.invokeWithArguments(x);
				}
			} catch (ClassCastException | ArrayIndexOutOfBoundsException e) {
				throw MatchException.unexpected(x, e);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
	
		@Override
		public String toString() {
			return name + ":" + methodHandle.type();
		}
	}

	/**
	 * Experimental
	 */
	private static class SyntaxFunction implements Syntax {
		private final MethodHandle methodHandle;
		private final String name;
	
		public SyntaxFunction(MethodHandle methodHandle,String name) {
			this.name = name;
			if(methodHandle.type().parameterCount()>2||
					!methodHandle.type().parameterType(0).equals(Env.class))
				throw new IllegalArgumentException(
						"expecting method with 2 arguments: Env,Object");
			if(methodHandle.type().parameterCount()==1)
				methodHandle = MethodHandles.dropArguments(methodHandle, 1, Object.class);
			
			
			
			
			
			methodHandle= wrapVoidRetWithUnitIfNecessary(methodHandle);
			this.methodHandle = methodHandle;
		}
	
		@Override
		public Object apply(Env env, Object ast) {
			try {
				return methodHandle.invokeWithArguments(env,ast);
			} catch (ClassCastException | ArrayIndexOutOfBoundsException e) {
				throw MatchException.unexpected(ast, e);
			} catch (RuntimeException e) {
			  throw e;
			} catch (Throwable e) {
               throw new Error(e);
            }
		}
		
		@Override
		public String toString() {
			return "syntax: " + name + ":" + methodHandle.type();
		}
	}

	public static <T extends PrimitiveProvider> void installPrimitives(
			Class<? extends T> clazz, T instance, Env env) {
		boolean takeAll = clazz.getAnnotation(PrimitivesClass.class) != null;
		Primitives.installFlFuncs(clazz, instance, env, takeAll,true,false);
	}

	private static <T> void installFlFunc(Method m, T instance, boolean takeAll,boolean ignoreNonStaticsWithoutInstance,boolean forceVarArgs,Env env) {
			Primitive a = m.getAnnotation(Primitive.class);
			if (!takeAll && a == null)
				 return;
	
			boolean isStatic = Modifier.isStatic(m.getModifiers());
			if (!Modifier.isPublic(m.getModifiers()))
				return;
			if (!isStatic && instance == null && ignoreNonStaticsWithoutInstance) {
				System.err.println("ignoring " + m);
				return;
			}
	
			MethodHandle methodHandle;
			try {
				methodHandle = MethodHandles.publicLookup().unreflect(m);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			if (!isStatic && instance!=null)
				methodHandle = methodHandle.bindTo(instance);
			String name = m.getName();
			if (a != null && !a.name().isEmpty()) {
				name = a.name();
			}
	
			boolean syntax = false;
			if (a != null) {
				syntax = a.syntax();
			}
	
			String alias = null;
			if (a != null && !a.alias().isEmpty()) {
				alias = a.alias();
			}
			
//			//need it?
			if (!syntax && methodHandle.type().parameterCount() > 0
					&& methodHandle.type().parameterType(0).equals(Env.class)) {
				methodHandle = methodHandle.bindTo(env);
			}
			
	
			boolean varArgs = forceVarArgs || methodHandle.type().parameterCount() > 1;
	
			try {
				final Object f = syntax ? new SyntaxFunction(methodHandle,name)
						: new PrimitiveFunction(methodHandle, name, varArgs);
				env.define(name, f);
				if (alias != null)
					env.define(alias, f);
	
			} catch (IllegalArgumentException e) {
				System.err.println(m.getName()
						+ " not defined: " + e.getMessage());
			}
	}

	static <T> void installFlFuncs(Class<? extends T> clazz, T instance, Env env,
			boolean takeAll,boolean ignoreNonStaticsWithoutInstance,boolean forceVarArgs) {
		for (Method m : clazz.getMethods()) {
			installFlFunc(m, instance, takeAll, ignoreNonStaticsWithoutInstance,forceVarArgs,env);
		}
		
		if(instance instanceof PrimitiveProvider){
			((PrimitiveProvider)instance).install(env);
		}
	}

	private static MethodHandle wrapVoidRetWithUnitIfNecessary(MethodHandle methodHandle) {
		if(methodHandle.type().returnType().equals(void.class)){
			methodHandle = MethodHandles.filterReturnValue(methodHandle, MethodHandles.constant(Unit.class, Unit.UNIT)); //UNIT
		}
		return methodHandle;
	}

}
