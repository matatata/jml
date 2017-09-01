package fl.eval;

import static fl.core.ToString.stringify;

@SuppressWarnings("serial")
public class MatchException extends RuntimeException{
	public MatchException(String msg){
		super(msg);
	}

	public MatchException(String msg, Throwable cause) {
		super(msg,cause);
	}
	
	public static MatchException doesNotMatch(Object formals, Object actualArg,Throwable e) {
		return new MatchException(stringify(actualArg) + " does not match " + stringify(formals),e);
	}

	public static MatchException doesNotMatch(Object formals, Object actualArg) {
		return doesNotMatch(formals, actualArg,null);
	}

	public static MatchException unexpected(Object arg, Throwable e) {
		return new MatchException("unexpected " + stringify(arg),e);
	}
}
