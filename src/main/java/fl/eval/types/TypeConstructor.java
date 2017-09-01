package fl.eval.types;

import java.util.Iterator;

import fl.core.Function;
import fl.core.ITupel;
import fl.core.ToString;
import fl.core.Unit;
import fl.eval.MatchException;
import fl.frontend.tast.Arrow;
import fl.frontend.tast.TypeSymbol;
import fl.frontend.tast.TypeVariable;
import fl.frontend.tast.TypeVisitor;

public class TypeConstructor implements Function<Object, Object>,
		ITypeConstructor {
	private final DataType dataType; // owner
	private final String name;
	private final Object texp; // e.g. int*string

	public TypeConstructor(DataType dataType, String name, Object texp) {
		if (texp == null)
			throw new IllegalArgumentException();
		this.dataType = dataType;
		this.name = name;
		this.texp = texp;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		if (texp == null)
			return name;
		return name + " of " + ToString.stringifyTypeExp(texp);
	}

	@Override
	public DataType getDataType() {
		return dataType;
	}

	@Override
	public Object apply(Object x) {
		new TypeConsTypeChecker().visit(texp, x);
		return createDataTypeInstance(x);
	}

	protected IDataTypeInstance createDataTypeInstance(Object x) {
		return new DataTypeInstance(this,x);
	}
	
	@Override
	public boolean isAssignableFrom(ITypeConstructor constructor) {
		return this==constructor;
	}

	/**
	 * this is not quite necessary once we have a proper type-checker/inference!
	 */
	public static class TypeConsTypeChecker extends
			TypeVisitor<Object, Object> {
		@Override
		public Object visitTypeSymbol(TypeSymbol sym, Object actual) {
			if(!sym.isInstance(actual))
				throw MatchException.doesNotMatch(sym, actual);
			return null;
		}

		@Override
		public Object visitTypeVariable(TypeVariable typeVar, Object actual) {
			return null;
		}

		@Override
		public Object visitArrow(Arrow exp, Object actual) {
			if (!(actual instanceof Function))
				throw MatchException.doesNotMatch(exp, actual, null);
			return null;
		}
		
		@Override
		protected Object visitUnit(Object actual) {
			 if (!(actual instanceof Unit))
				 throw MatchException.doesNotMatch(Unit.UNIT, actual, null);
			 return null;
		}

		@Override
		protected Object visitDataType(DataType exp, Object actual) {
			if(!exp.isInstance(actual))
				throw MatchException.doesNotMatch(exp, actual, null);
			return null;
		}
		
		@Override
		protected Object visitTypeAbbreviation(TypeAbbreviation exp, Object ctx) {
			return visit(exp.getType(), ctx);
		}

		@Override
		@SuppressWarnings("rawtypes")
		public Object visitProduct(ITupel exp, final Object actual) {
			if (exp.length() != ((ITupel) actual).length())
				throw MatchException.doesNotMatch(exp, actual, null);
			Iterator l = exp.iterator();
			Iterator r = ((ITupel) actual).iterator();
			while (l.hasNext()) {
				visit(l.next(), r.next());
			}
			return null;
		}
	}
	
	@Override
	public boolean isInstance(Object x) {
		return TypeConstructor.isInstance(x, this);
	}
	
	public static boolean isInstance(Object x,ITypeConstructor c) {
		if(!(x instanceof IDataTypeInstance))
			return false;
		return c.isAssignableFrom(((IDataTypeInstance)x).getConstructor());
	}


	public static class NullableTypeInstance implements ITypeConstructor,
			IDataTypeInstance {
		private final String name;
		private final DataType dataType; // owner

		public NullableTypeInstance(DataType dataType, String name) {
			this.name = name;
			this.dataType = dataType;
		}
		
		@Override
		public boolean isInstance(Object x) {
			return TypeConstructor.isInstance(x, this);
		}

		@Override
		public String toString() {
			return getName();
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public ITypeConstructor getConstructor() {
			return this;
		}

		@Override
		public DataType getDataType() {
			return dataType;
		}

		@Override
		public Object getData() {
			return Unit.UNIT;
		}
		
		@Override
		public boolean isAssignableFrom(ITypeConstructor constructor) {
			return this==constructor;
		}
	}

	@Override
	public ITypeConstructor getConstructor() {
		return this;
	}

	@Override
	public Object getData() {
		return this;
	}
}
