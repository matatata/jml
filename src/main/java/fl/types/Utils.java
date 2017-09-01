package fl.types;

import fl.core.Function;

public class Utils {

	@SuppressWarnings("unchecked")
	public static <A, B, C> Function<A, C> compose(
			final Function<B, C> a, final Function<A, B> b) {

		if(a==Function.ID)
			return (Function<A, C>) b;
		if(b==Function.ID)
			return (Function<A, C>) a;
		
		return new Function<A, C>() {
			@Override
			public C apply(A x) {
				return a.apply(b.apply(x));
			}

			@Override
			public String toString() {
				return a + " o " + b;
			}
		};
	}
	
		
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Function cc(Function... c) {
		Function x = Function.ID;
		for(Function o2o : c){
			x=compose(x,o2o);
		}
		return x;
	}
}
