package fl.eval.types;

import static fl.core.ToString.stringifyTypeExp;

public class TypeAbbreviation {
	private final String name;
	private final Object type;

	public TypeAbbreviation(String name, Object type) {
		this.name = name;
		this.type = type;
	}

	@Override
	public String toString() {
		return name + "=" + stringifyTypeExp(getType());
	}

	public String getName() {
		return name;
	}

	public Object getType() {
		return type;
	}
}
