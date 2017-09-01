package fl.core;

@SuppressWarnings("serial")
public class EmptyListException extends RuntimeException {
	public EmptyListException() {
		super("Empty!");
	}
}
