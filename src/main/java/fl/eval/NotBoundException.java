package fl.eval;

@SuppressWarnings("serial")
public class NotBoundException extends RuntimeException {


	public NotBoundException(String symbol) {
		super(symbol + " not bound!");
	}


}
