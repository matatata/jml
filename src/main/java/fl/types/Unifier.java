package fl.types;

import static fl.types.Substitution.CIRCULARITY;
import static fl.types.Substitution.replace;
import static fl.types.Utils.compose;

import org.apache.commons.lang.ObjectUtils;

import fl.core.Function;
import fl.core.ILst;
import fl.core.Lists;
import fl.frontend.tast.Arrow;
import fl.frontend.tast.TypeSymbol;
import fl.frontend.tast.TypeVariable;

public class Unifier {
	
	/*
	 * (*  unify : typ * typ -> (typ -> typ)  *)

fun unify (VAR a, t) =
      if VAR a = t then identity
      else if occurs (a, t) then raise Circularity
      else replace (a, t)
|   unify (t, VAR a)   = unify (VAR a, t)
|   unify (INT, INT)   = identity
|   unify (BOOL, BOOL) = identity
|   unify (ARROW(t1, t2), ARROW(t3, t4)) =
      let val s1 = unify (t1, t3)
	  val s2 = unify (s1 t2, s1 t4)
      in
	  s2 o s1
      end
|   unify (_, _) = raise Mismatch

	 */
	@SuppressWarnings({ "unchecked" })
	public static Function<Object, Object> unify(Object x,
			Object y) {
		if(x instanceof String){ // tVAR
			if(x.equals(y))
				return Function.ID;
			else if(occurs((String) x, y)){
				return CIRCULARITY;
			}
			else {
				return replace((String) x, y);
			}
		}
		if(y instanceof String){
			return unify(y, x);
		}
		
		if(x instanceof Arrow && y instanceof Arrow){
			Arrow fnx = (Arrow)x;
			Arrow fny = (Arrow)y;
			Function<Object, Object> s1 = unify(fnx.from, fny.from);
			Function<Object, Object> s2 = unify(fnx.to, fny.to);
			return compose(s2, s1);
		}
		
		if(x instanceof ILst && y instanceof ILst){
			ILst px=(ILst) x;
			ILst py=(ILst) y;
			if(px.isNil() && py.isNil())
				return Function.ID;
			Function<Object, Object> s1 = unify(px.head(), py.head());
			Function<Object, Object> s2 = unify(px.tail(), py.tail());
			return compose(s2, s1);
		}
		
		if (ObjectUtils.equals(x, y))
			return Function.ID;
		
		return new Substitution.Failure(x,y);
	}

//	public Substitution unify(Object x, Object y, Substitution theta) {
//		if (theta == FAILURE)
//			return FAILURE;
//		if (ObjectUtils.equals(x, y))
//			return theta;
//		else if (x instanceof String) {
//			return unifyVar((String) x, y, theta);
//		} else if (y instanceof String) {
//			return unifyVar((String) y, x, theta);
//		}
//		// else if(x instanceof Product && y instanceof Product){
//		//
//		// }
//		// else if(x instanceof Pair && y instanceof Pair){
//		// Pair xl = (Pair)x;
//		// Pair yl = (Pair)y;
//		// return unify(xl.rest, yl.rest, unify(xl.head, yl.head, theta));
//		// }
//		// else if(x.isFunction()&&y.isFunction()){
//		// Function xf = (Function)x;
//		// Function yf = (Function)y;
//		// return unify(xf.args, yf.args, unify(xf.ops, xf.ops, theta));
//		// }
//		// else if(both constants... find common subtype(s)){
//		//
//		// }
//		else
//			return FAILURE;
//	}
//
//	private Substitution unifyVar(String var, Object x, Substitution theta) {
//		if (theta.containsKey(var)) {
//			return unify(theta.get(var), x, theta);
//		} else if (theta.containsKey(x)) {
//			return unify(var, theta.get(x), theta);
//		} else if (occurs(var, x))
//			return FAILURE;
//		else {
//			// TODO cascadeSubstitution
//			Substitution newS = new Substitution();
//			newS.putAll(theta);
//			newS.put(var, x);
//			return newS;
//		}
//	}
//
//	//
//	// // See:
//	// //
//	// http://logic.stanford.edu/classes/cs157/2008/miscellaneous/faq.html#jump165
//	// // for need for this.
//	 private Substitution cascadeSubstitution(Substitution theta,
//	 String var, Object x) {
//	 theta.put(var, x);
//	 for (Map.Entry<String, Object> e : theta.entrySet()) {
//	 // theta.put(v, _substVisitor.subst(theta, theta.get(v)));
//		 theta.put(key, value)
//	 }
//	 // Ensure Function Terms are correctly updates by passing over them
//	 // again. Fix for testBadCascadeSubstitution_LCL418_1()
//	 for (Variable v : theta.keySet()) {
//	 Term t = theta.get(v);
//	 if (t instanceof Function) {
//	 theta.put(v, _substVisitor.subst(theta, t));
//	 }
//	 }
//	 return theta;
//	 }

	

	/*
	 * fun occurs (a, VAR b) = (a = b) | occurs (a, ARROW(t1, t2)) = occurs (a,
	 * t1) orelse occurs (a, t2) | occurs (a, _) = false
	 */
	static boolean occurs(final String a, Object x) {
		return new Visitor<Boolean, Object>() {
			@Override
			protected Boolean visitPoduct(ILst x, Object ctx) {
				ILst l = x;
				while (l != Lists.nil()) {
					if (visit(l.head(), ctx))
						return true;
					l = l.tail();
				}
				return false;
			}

			@Override
			protected Boolean visitConst(TypeVariable x, Object ctx) {
				return false;
			}
			protected Boolean visitConst(TypeSymbol x, Object ctx) {
				return false;
			};

			@Override
			protected Boolean visitFn(Arrow x, Object ctx) {
				return visit(x.from, ctx) || visit(x.to, ctx);
			}

			@Override
			protected Boolean visitTId(String x, Object ctx) {
				return a.equals(x);
			}
		}.visit(x, null);
	}

}
