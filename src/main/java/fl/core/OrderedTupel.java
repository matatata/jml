package fl.core;
import java.util.Arrays;
import java.util.Iterator;

import fl.core.Tupels.OrderedTupelIterator;
import fl.eval.types.ITypeConstructor;
import fl.frontend.ast2.IAstNode;

@SuppressWarnings({ "rawtypes", "unchecked" })
class OrderedTupel implements IOrderedTupel,IAstNode,Iterable{
	private final Object[] elems;

	@SafeVarargs
	protected OrderedTupel(Object... elems) {
		if(elems==null || elems.length==1)
			throw new IllegalArgumentException();
		this.elems = elems;
	}
	
	@Override
	public int length(){
		return elems.length;
	}
	
	
	@Override
	public <T> T at(int i){
		return (T) elems[i];
	}
	
	@Override
	public <T> T first() {
		return at(0);
	}
	
	@Override
	public <T> T second() {
		return at(1);
	}
	
	@Override
	public String toString() {
		return ToString.stringifyTupel(this);
	}

	@Override
	public Iterator iterator() {
		return new OrderedTupelIterator(this);
	}
	
	/**
	 * @return underlying Array for direct access.
	 */
	@Override
	public Object[] array() {
		return elems;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(elems);
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
		OrderedTupel other = (OrderedTupel) obj;
		if (!Arrays.equals(elems, other.elems))
			return false;
		return true;
	}

	@Override
	public ITupel map(Function f) {
		Object[] k=new Object[length()];
		for(int i=0;i<k.length;i++){
			k[i]=f.apply(at(i));
		}
		return new OrderedTupel(k);
	}

	@Override
	public ITypeConstructor getConstructor() {
		return TupelConstructor.tupel;
	}

	@Override
	public Object getData() {
		return this;
	}
	
	
	
}
