package fl.frontend.tast;
import static fl.core.ToString.stringifyTypeExp;
public class Arrow {
	public final Object from,to;

	public Arrow(Object from, Object to) {
		this.from = from;
		this.to = to;
	}
	
	@Override
	public String toString() {
		return "("+stringifyTypeExp(from) + "->" + stringifyTypeExp(to) + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Arrow other = (Arrow) obj;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;
		return true;
	}
	
	
}
