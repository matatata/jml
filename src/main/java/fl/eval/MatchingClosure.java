package fl.eval;

import fl.core.Function;
import fl.core.ILst;
import fl.core.IOrderedTupel;
import fl.core.Lists;

public class MatchingClosure<T, R> implements Function<T, R> {
	private Env capturedEnv;
	private ILst cases;

	public MatchingClosure(Env capturedEnv, ILst cases) {
		this.capturedEnv = capturedEnv;
		this.cases = cases;
	}

	@Override
	public R apply(T arg) {
		ILst x = cases;
		while (x != Lists.nil()) {
			IOrderedTupel caze = x.head();
			try {
				return new Closure<T, R>(capturedEnv, caze.at(0), caze.at(1))
						.apply(arg);
			} catch (MatchException e) {
				// skip
//				System.err.println("skipping " + e.getMessage());
			}
			x = x.tail();
		}
		throw MatchException.unexpected(arg, null);
	}

}
