package fl.core;

import java.util.Iterator;

import fl.core.Tupels.OrderedTupelIterator;
import fl.eval.types.ITypeConstructor;
import fl.frontend.ast2.IAstNode;
@SuppressWarnings({ "rawtypes", "unchecked" })
class BinaTupel implements IBinaTupel, IAstNode {
	private final Object left;
	private final Object right;
	
	BinaTupel(Object l,Object r) {
		this.left=l;
		this.right=r;
	}

	@Override
	public <T> T at(int k) {
		switch(k){
		case 0:
			return (T) left;
		case 1:
			return (T) right;
		}
		throw new IndexOutOfBoundsException(String.valueOf(k));
	}

	@Override
	public int length() {
		return 2;
	}

	@Override
	public <T> T first() {
		return (T) left;
	}

	@Override
	public <T> T second() {
		return (T) right;
	}
	
	@Override
	public Iterator iterator() {
		return new OrderedTupelIterator<Object>(this); 
	}

	@Override
	public Object[] array() {
		return new Object[]{left,right};
	}
	
	@Override
	public String toString() {
		return ToString.stringifyTupel(this);
	}
	
	
	@Override
	public ITupel map(Function f) {
		return new BinaTupel(f.apply(left), f.apply(right));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
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
		BinaTupel other = (BinaTupel) obj;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		return true;
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
