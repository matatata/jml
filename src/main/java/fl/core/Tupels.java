package fl.core;



public class Tupels {

	public static IBinaTupel of(Object a,Object b){
		return new BinaTupel(a,b);
	}
	
	@SafeVarargs
	public static IOrderedTupel of(Object...elems){
		if(elems==null || elems.length<=1)
			throw new IllegalArgumentException("unexpected " + elems);
		if(elems.length==2)
			return Tupels.of(elems[0],elems[1]);
		return new OrderedTupel(elems);
	}


	public static <T> T at(Object tupel,int i){
		return ((IOrderedTupel)tupel).at(i);
	}

	
	public static <T> T first(Object tupel){
		return ((IOrderedTupel)tupel).first();
	}
	
	public static <T> T second(Object tupel){
		return ((IOrderedTupel)tupel).second();
	}
	
	static class OrderedTupelIterator<T> implements java.util.Iterator<T>{
		private IOrderedTupel t;
		private int c=0;
		
		public OrderedTupelIterator(IOrderedTupel t) {
			this.t=t;
		}
		
		@Override
		public boolean hasNext() {
			return c<t.length();
		}

		@Override
		public T next() {
			return t.at(c++);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
	
}
