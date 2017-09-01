package fl.core;

import fl.eval.Env;
import fl.eval.types.DataType;
import fl.eval.types.ITypeConstructor;
import fl.eval.types.TypeConstructor;

@SuppressWarnings("rawtypes")
public class LstConstructor implements ITypeConstructor, Function {
	public static final DataType listT;
	public static final ITypeConstructor cons;
	
	static {
		listT=new DataType("list");
		cons=new LstConstructor();
		listT.setConstructors(Lists.of(cons));
	}
	
	public static void install(Env env){
		env.addTypeDef(listT.getName(), listT);
		env.define("cons", cons);
	}
	
	private LstConstructor() {
	}

	@Override
	public String getName() {
		return "::";
	}

	@Override
	public DataType getDataType() {
		return listT;
	}
	

	@Override
	public Object apply(Object x) {
		Object second = Tupels.second(x);
		if(!(second instanceof ILst))
			throw new IllegalArgumentException("second argument is not a list: " + second);
			
		return Lists.cons(Tupels.first(x), (ILst)second);
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public boolean isInstance(Object x) {
		return TypeConstructor.isInstance(x, this);
	}
	
	@Override
	public boolean isAssignableFrom(ITypeConstructor constructor) {
		return this==constructor;
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