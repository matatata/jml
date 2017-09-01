package fl.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fl.eval.types.IDataTypeInstance;
import fl.eval.types.ITypeConstructor;



@SuppressWarnings("rawtypes")
class Lst extends BinaTupel implements ILst, IDataTypeInstance {
	protected Lst(Object head, ILst tail) {
		super(head,tail);
	}



	@Override
	public Iterator iterator() {
		return new LstIterator(this);
	}

	@Override
	public <T> T head() {
		return first();
	}

	@Override
	public ILst tail() {
		return second();
	}

	@Override
	public String toString() {
		return ToString.stringifyLst(this);
	}

	private class LstIterator implements java.util.Iterator{
		private ILst lst;

		LstIterator(Lst lst) {
			this.lst = lst;
		}

		@Override
		public boolean hasNext() {
			return !lst.isNil();
		}

		@Override
		public Object next() {
			Object next = lst.head();
			lst = lst.tail();
			return next;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public ILst map(Function f) {
		if(isNil())
			return this;
		return new Lst(f.apply(head()), tail().map(f));
	}

	@Override
	public boolean isNil() {
		return false;
	}


	@Override
	public Object[] array() {
		List<Object> result = new ArrayList<>();
		ILst l = this;
		while (!l.isNil()) {
			result.add(l.head());
			l = l.tail();
		}
		return result.toArray();
	}


	@Override
	public int length() {
		return 2;
	}


	@Override
	public ITypeConstructor getConstructor() {
		return LstConstructor.cons;
	}


	@Override
	public Object getData() {
		return this;
	}
}
