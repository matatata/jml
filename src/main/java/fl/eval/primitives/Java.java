package fl.eval.primitives;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dynalang.dynalink.DefaultBootstrapper;
import org.dynalang.dynalink.beans.StaticClass;

import fl.core.Function;
import fl.core.IBinaTupel;
import fl.core.IOrderedTupel;
import fl.core.ToString;
import fl.core.Unit;
import fl.eval.Env;

public class Java implements PrimitiveProvider {

	@SuppressWarnings("rawtypes")
	private static final class DotFunction implements Function {
		private final String selector;
		private final Object rcvr;
		private final Object callable;

		private DotFunction(String selector, Object rcvr) {
			this.selector = selector;
			this.rcvr = rcvr;

			MethodHandle getMethod = createDynamicInvoker("dyn:getMethod:"
					+ selector, Object.class, Object.class);
			try {
				this.callable = getMethod.invokeExact(rcvr);

			} catch (Throwable e) {
				throw new RuntimeException(e);
			}

		}

		@Override
		public String toString() {
			return ToString.stringify(rcvr) + "." + selector;
		}

		@Override
		public Object apply(Object args) {
			List<Object> arguments = new ArrayList<>();
			arguments.add(callable);
			arguments.add(rcvr);

			if (args instanceof IOrderedTupel) {
				arguments.addAll(Arrays.asList(((IOrderedTupel) args).array()));
			} else if (!(args instanceof Unit)) {
				arguments.add(args);
			}

			Class<?>[] argTypes = mkTypeArray(arguments,1);
			try {
				MethodHandle invoker = createDynamicInvoker("dyn:call:"
						+ selector, Object.class, argTypes);
				return invoker.invokeWithArguments(arguments);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}

	}

	@Deprecated
	private static Class<?>[] mkTypeArray(int size) {
		Class<?>[] argTypes = new Class[size];
		Arrays.fill(argTypes, Object.class);
		return argTypes;
	}

	private static Class<?>[] mkTypeArray(List<Object> args,int offset) {
		Class<?>[] argTypes = new Class[args.size()];
		for (int i = 0; i < argTypes.length; i++) {
			Object arg = args.get(i);
			if(i<offset){
				argTypes[i]= Object.class;
			}
			else {argTypes[i] = arg == null ? Object.class : arg.getClass();}
		}
		return argTypes;
	}

	private static MethodHandle createDynamicInvoker(String operation,
			Class<?> returnType, Class<?>... parameterTypes) {
		return DefaultBootstrapper.publicBootstrap(null, operation,
				MethodType.methodType(returnType, parameterTypes))
				.dynamicInvoker();
	}

	@Primitive
	public static Class<?> classForName(String name)
			throws ClassNotFoundException {

		String[] searchPackages = { null, "java.lang" };

		for (String pkg : searchPackages) {
			try {
				String qname = pkg != null ? pkg + "." + name : name;
				return Class.forName(qname, false, Java.class.getClassLoader());
			} catch (ClassNotFoundException e) {
				continue;
			}
		}
		throw new ClassNotFoundException(name);
	}

	@Primitive(alias = "class")
	public static Object staticClassForName(String name)
			throws ClassNotFoundException {
		return StaticClass.forClass(classForName(name));
	}

	@Primitive
	public static Object importClass(Env env, Class<?> cl)
			throws ClassNotFoundException {
		Primitives.installFlFuncs(cl, null, env, true, false, false);
		return Unit.UNIT;
	}

	@Primitive
	public static Object importObject(Env env, Object obj)
			throws ClassNotFoundException {
		Primitives.installFlFuncs(obj.getClass(), obj, env, true, false, true);
		return Unit.UNIT;
	}

	@Primitive(name = "new")
	public static Object newInstance(final Object args) {
		return newInstance(new ArrayList<Object>(), args);
	}

	private static Object newInstance(List<Object> arguments, final Object args) {
		if (args instanceof IOrderedTupel) {
			arguments.addAll(Arrays.asList(((IOrderedTupel) args).array()));
		} else if (!(args instanceof Unit)) {
			arguments.add(args);
		}
		MethodHandle invokeCtor = createDynamicInvoker("dyn:new", Object.class,
				mkTypeArray(arguments,1));
		// mkTypeArray(arguments.size()));
		try {
			return invokeCtor.invokeWithArguments(arguments);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("rawtypes")
	@Primitive(name = ".")
	public static Object dot(final Object x) {
		final Object rcvr = ((IBinaTupel) x).first();
		final String selector = ((IBinaTupel) x).second();

		if (selector.equals("new") && rcvr instanceof StaticClass) {
			return new Function() {
				@Override
				public Object apply(Object args) {
					List<Object> arguments = new ArrayList<>();
					arguments.add(rcvr);
					return newInstance(arguments, args);
				}
			};
		}

		MethodHandle getProp = createDynamicInvoker("dyn:getProp:" + selector,
				Object.class, Object.class);
		try {
			return getProp.invokeExact(rcvr);
		} catch (Throwable e) {
			return new DotFunction(selector, rcvr);
		}
	}

	@Override
	public void install(Env env) {
		// nothing to do
	}
}
