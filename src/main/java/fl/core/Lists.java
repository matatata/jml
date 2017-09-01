package fl.core;

import java.util.Iterator;
import java.util.List;

import fl.eval.types.ITypeConstructor;
import fl.frontend.ast2.IAstNode;

@SuppressWarnings("rawtypes")
public class Lists {

	private static Iterator EmptyIterator=new Iterator() {
		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public Object next() {
			throw new IllegalStateException();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}; 
	
	static ILst NIL = new NilLst();
	
	static class NilLst implements ILst, IAstNode{
		@Override
		public Iterator iterator() {
			return EmptyIterator;
		}

		@Override
		public <T> T at(int i) {
			throw new EmptyListException();
		}
		
		@Override
		public <T> T head() {
			throw new EmptyListException();
		}
		
		@Override
		public <T> T first() {
			throw new EmptyListException();
		}
		@Override
		public <T> T second() {
			throw new EmptyListException();
		}

		@Override
		public ILst tail() {
			throw new EmptyListException();
		}

//		@Override
//		public Object[] toArray() {
//			throw new EmptyListException();
//		}

		@Override
		public boolean isNil() {
			return true;
		}

		@Override
		public ILst map(Function f) {
			return this;
		}

		@Override
		public Object[] array() {
			return new Object[0];
		}


		@Override
		public int length() {
			return 0;
		}
		
		@Override
		public String toString() {
			return "nil";
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
	
	public static <T> ILst nthtl(ILst list, int n) {
		for (; 0 < n; --n)
			list = list.tail();
		return list;
	}

	public static <T> T nthhd(ILst list, int n) {
		return nthtl(list, n).head();
	}

	@SafeVarargs
	public static <T> ILst of(T... args) {
		if(args==null || args.length==0)
			return nil();
		ILst x = nil();
		for (int i = args.length - 1; i >= 0; i--) {
			x = new Lst(args[i], x);
		}
		return x;
	}

	public static <T> ILst of(List<T> args) {
		if(args==null || args.isEmpty())
			return nil();
		ILst x = nil();
		for (int i = args.size() - 1; i >= 0; i--) {
			x = new Lst(args.get(i), x);
		}
		return x;
	}


	public static <T> ILst cons(T head,ILst tail){
		return new Lst(head,tail);
	}
	
	public static <T> ILst append(T item,ILst list){
		if(list.isNil())
			return Lists.of(item);
		return new Lst(list.head(),append(item,list.tail()));
	}

	public static ILst nil() {
		return NIL;
	}

	public static boolean isNil(Object x) {
		return x==NIL;
	}
}
