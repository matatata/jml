package fl.types;

import fl.core.Function;

public abstract class Environment{

	public static Function<String, Object> EMPTY = new Function<String, Object>() {
		@Override
		public Object apply(String tvar) {
			throw new UnboundId(tvar);
		}

		@Override
		public String toString() {return "empty";}
	};

	public static Function<String, Object> update(
			final Function<String, Object> E, final String x,
			final Object ty) {
		return new Function<String, Object>() {

			@Override
			public Object apply(String y) {
				return x.equals(y) ? ty : E.apply(y);
			}

			@Override
			public String toString() {
				return E + "," + x + ":" + ty;
			}
		};
	}

}
