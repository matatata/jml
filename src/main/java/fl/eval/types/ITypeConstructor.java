package fl.eval.types;

public interface ITypeConstructor extends IDataTypeInstance {
	String getName();
	DataType getDataType();
	boolean isAssignableFrom(ITypeConstructor constructor);
	boolean isInstance(Object x);
	
	
//	public static ITypeConstructor META_CONSTRUCTOR=new 
}