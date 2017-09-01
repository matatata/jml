package fl.eval;

import fl.core.Function;


class Closure<X,Y> implements Function<X,Y> {
	private final Env capturedEnv;
	private final Object formals;
	private final Object code;

	public Closure(Env capturedEnv,Object formals, Object code) {
		this.capturedEnv = capturedEnv;
		this.formals = formals;
		this.code = code;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Y apply(X arg){
		Evaluator evaluator = new Evaluator();
		Env env = new Env(capturedEnv);
		evaluator.bindArgs(formals, arg, env);
		return (Y) evaluator.visit(code, env);
	}
	
	@Override
	public String toString() {
		return "_fn";
	}

}
