package fl.core;

@SuppressWarnings("rawtypes")
public interface ITupel extends Iterable{
	int length();
	Object[] array();
	ITupel map(Function f);
}
