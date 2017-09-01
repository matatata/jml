package fl.core;

import fl.eval.Env;
import fl.eval.types.DataType;
import fl.eval.types.ITypeConstructor;
import fl.eval.types.TypeConstructor;

@SuppressWarnings("rawtypes")
public class TupelConstructor implements ITypeConstructor, Function {
	public static final DataType tupelT;
	public static final ITypeConstructor tupel;
	
	static {
		tupelT=new DataType("tupel");
		tupel=new TupelConstructor();
		tupelT.setConstructors(Lists.of(tupel));
	}
	
	public static void install(Env env){
		env.addTypeDef(tupelT.getName(), tupelT);
		env.define(tupelT.getName(), tupel);
	}
	
	private TupelConstructor() {
	}

	@Override
	public String getName() {
		return "tupel";
	}

	@Override
	public DataType getDataType() {
		return tupelT;
	}

	@Override
	public Object apply(Object x) {
		if(((ILst)x).isNil())
			return Unit.UNIT;
		return Tupels.of(((ILst)x).array());
	}
	
	@Override
	public String toString() {
		return "tupel";
	}
	
	@Override
	public boolean isInstance(Object x) {
		return TypeConstructor.isInstance(x, this);
	}
	
	/**
	 * This is a special case, since a List is a Tupel too.
	 */
	@Override
	public boolean isAssignableFrom(ITypeConstructor constructor) {
		return this==constructor || constructor == LstConstructor.cons;
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