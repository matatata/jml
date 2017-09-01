package fl.types;

import static fl.core.Function.ID;
import static fl.types.Utils.cc;
import fl.core.Function;
import fl.core.ILst;
import fl.core.IOrderedTupel;
import fl.core.IRecord;
import fl.frontend.ast.AstVisitor;
import fl.frontend.ast.Symbol;
import fl.frontend.tast.Arrow;
import fl.frontend.tast.TypeSymbol;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class TypeInferrer extends AstVisitor<S_T, Function<String, Object>> {

	@Override
	public S_T visitTan(Object exp, Object texp, Object exp2,
			Function<String, Object> ctx) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected S_T visitRecord(IRecord exp, Function<String, Object> ctx) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public S_T visitDataTypeDecl(String sym, ILst typeconsts, Function<String, Object> ctx) {
		return null;
	}
	
	@Override
	public S_T visitTypeDecl(String sym, Object val, Function<String, Object> ctx) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public S_T visitSeq(ILst exps, Function<String, Object> ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public S_T visitIf(Object e1, Object e2, Object e3, Object original,
			Function<String, Object> E) {
		S_T s1_t1 = visit(e1, E);
		Function s2 = Unifier.unify(s1_t1.t, new TypeSymbol(Boolean.class));
		S_T s3_t2 = visit(e2, cc(s2, s1_t1.s, E));
		S_T s4_t3 = visit(e3, cc(s3_t2.s, s2, s1_t1.s, E));
		Function s5 = Unifier.unify(s4_t3.s.apply(s3_t2.t), s4_t3.t);

		return new S_T(cc(s5, s4_t3.s, s3_t2.s, s2, s1_t1.s), s5.apply(s4_t3.t));
	}

	@Override
	public S_T visitConst(Object f, Function<String, Object> E) {
		return new S_T(ID, new TypeSymbol(f.getClass()));
	}

	@Override
	public S_T visitId(Symbol x, Function<String, Object> E) {
		/*
		 * E(x) = t ---------- E |- x : t *)
		 */
		return new S_T(ID, E.apply(x.image));
	}

	
	@Override
	public S_T visitTupel(IOrderedTupel t, Function<String, Object> E) {
		Object[] types = new Object[t.length()];
		int i = t.length() - 1;
		S_T s_t = visit(t.at(i), E);
		types[i] = s_t.t;
		for (i = t.length() - 2; i >= 0; i--) {
			Object e = t.at(i);
			S_T s1_t1 = visit(e, cc(s_t.s, E));
			types[i] = s1_t1.t;
			s_t = new S_T(cc(s1_t1.s, s_t.s), s1_t1.t);
		}
		return new S_T(s_t.s, fl.core.Lists.of(types));
	}

	@Override
	public S_T visitList(ILst t, Function<String, Object> E) {
		// TODO Auto-generated method stub
		return null;
	}

	public long tvarCount = 0;

	private String newTVar() {
		return "'" + tvarCount++;
	}

	@Override
	public S_T visitFn(ILst cases, Object original,
			Function<String, Object> E) {
		/*
		 * (* E[x : t1] |- e : t2 ------------------------- E |- fn x => e : t1
		 * -> t2 *) let val t1 = newtypevar() val (s, t2) = W (update E x t1, e)
		 * in (s, s (ARROW (t1, t2))) end
		 */

		IOrderedTupel f_e = cases.head();

		String t1 = newTVar();

		//TODO visit formals and update Environment
		//TODO make a visitor adapter
		
		Object formals = f_e.at(0);
		Function<String, Object> newE = Environment.update(E, ((Symbol) formals).image, t1);
		S_T s_t2 = visit(f_e.at(1),
				newE);
		Function s = s_t2.s;
		return new S_T(s, s.apply(new Arrow(t1, s_t2.t)));
	}

	@Override
	public S_T visitApp(Object fun, Object arg, Object original,
			Function<String, Object> E) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public S_T visitVal(Object formals, Object val, boolean rec,
			Object original, Function<String, Object> E) {

		// return new S_T(Environment.update(ctx, sym, val),);
		return null;
	}
	
	@Override
	public S_T visitLet(ILst bindings, Object exp,
			Function<String, Object> ctx) {
		// TODO Auto-generated method stub
		return null;
	}

}
