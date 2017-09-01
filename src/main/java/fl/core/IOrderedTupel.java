package fl.core;

public interface IOrderedTupel extends ITupel{
	/**
	 * 
	 * @param i 0,....,n
	 * @return
	 */
	<T> T at(int i);
	<T> T first();
	<T> T second();
}
