package fl.eval.types;

/**
 * 
 * 
 * should not be public it's a detail!?
 */
public interface IDataTypeInstance {
	ITypeConstructor getConstructor();
	Object getData();
}