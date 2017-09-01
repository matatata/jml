package fl.eval.types;

public class DataTypeInstance implements IDataTypeInstance {
	private final ITypeConstructor constructor;
	private final Object data;

	public DataTypeInstance(ITypeConstructor constructor,Object data) {
		this.constructor=constructor;
		this.data = data;
	}

	@Override
	public ITypeConstructor getConstructor() {
		return constructor;
	}

	public DataType getDataType() {
		return getConstructor().getDataType();
	}

	@Override
	public Object getData() {
		return data;
	}

	@Override
	public String toString() {
		return getConstructor().getName() + " " + getData();
	}
}