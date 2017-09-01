package fl.types;

import static fl.core.ToString.stringifyTypeExp;
import fl.core.Function;

@SuppressWarnings("rawtypes")
public class S_T {
	public S_T(Function s, Object t) {
		super();
		this.s = s;
		this.t = t;
	}

	public final Function s;
	public final Object t;
	
	@Override
	public String toString() {
		return "("+s + ", " + stringifyTypeExp(t)+ ")";
	}
}
