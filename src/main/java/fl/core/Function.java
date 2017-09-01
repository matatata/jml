package fl.core;

/**
 * 
 * f: X->Y 
 * @param <X> Domain
 * @param <Y> Codomain
 */
public interface Function<X, Y> {
	Y apply(X x);
	
	@SuppressWarnings("rawtypes")
	public static Function ID=new Function(){
		@Override
		public Object apply(Object x) {
			return x;
		}
		
		@Override
		public String toString() {return "identity";}
	};

}
