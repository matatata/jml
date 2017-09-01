package fl.eval.types;

import fl.core.ILst;

public class DataType {
	private final String name;
	private ILst constructors;

	public DataType(String name) {
		this.name = name;
	}

	@SuppressWarnings("unchecked")
	public Iterable<ITypeConstructor> getConstructors() {
		return constructors;
	}

	public void setConstructors(ILst constructors) {
		this.constructors = constructors;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	public boolean isInstance(Object actual) {
		if(!(actual instanceof IDataTypeInstance)){
			for(ITypeConstructor c : getConstructors()){
				if(c.isInstance(actual))
					return true;
			}
			return false;
		}
		
		DataType actualDataType = ((IDataTypeInstance) actual).getConstructor().getDataType();
		return isCompatibleDataType(actualDataType);
	}

	protected boolean isCompatibleDataType(DataType actualDataType) {
		return actualDataType == this;
	}
}
