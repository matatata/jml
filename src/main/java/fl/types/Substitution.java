package fl.types;

import fl.core.Function;
import fl.core.ILst;
import fl.frontend.tast.Arrow;
import fl.frontend.tast.TypeSymbol;
import fl.frontend.tast.TypeVariable;

@SuppressWarnings("rawtypes")
public class Substitution {

	public static class Failure implements Function {
		private Object left;
		private Object right;

		public Failure(Object x, Object y) {
			this.left = x;
			this.right = y;
		}

		@Override
		public Object apply(Object x) {
			throw new MismatchException(this.left + " not unifyable with " + this.right  + " see also " + x);
		}

		@Override
		public String toString() {return "Failure";}
	}

	private Substitution(){};

	
	

	public static Function CIRCULARITY = new Function() {
		@Override
		public Object apply(Object x) {
			throw new CircularityException();
		}
		@Override
		public String toString() {return "CIRCULARITY";}
	};
	
	public static Function replace(final String a, final Object t) {
		return new Function() {
			@Override
			public Object apply(Object x) {
				return replace(a, t, x);
			}
			@Override
			public String toString() {return a + "/" + t;}
		};
	}
	
	/**
	 * 
	 * @param a
	 * @param t
	 * @param x
	 * @return copy of x where a replace with t
	 */
	@SuppressWarnings({ "unchecked" })
	private static Object replace(final String a, final Object t, Object x) {
		return new Visitor() {
			@Override
			protected Object visitPoduct(ILst x, final Object ctx) {
				return x.map(new Function() {
					@Override
					public Object apply(Object obj) {
						return visit(obj, ctx);
					}
				});
			}

			@Override
			protected Object visitConst(TypeVariable x, Object ctx) {
				return x;
			}
			
			@Override
			protected Object visitConst(TypeSymbol x, Object ctx) {
				//????
				return x;
			}

			@Override
			protected Object visitFn(Arrow x, Object ctx) {
				return new Arrow(visit(x.from, ctx), visit(x.to, ctx));
			}

			@Override
			protected Object visitTId(String x, Object ctx) {
				return a.equals(x) ? t : x;
			}
		}.visit(x, null);
	}
}