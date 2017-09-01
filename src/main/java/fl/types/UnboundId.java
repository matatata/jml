package fl.types;

public class UnboundId extends RuntimeException {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnboundId(String id) {
		super("Unbound " + id);
	}

}
