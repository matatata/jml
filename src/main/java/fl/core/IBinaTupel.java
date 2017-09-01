package fl.core;

public interface IBinaTupel extends IOrderedTupel{
	<T> T first();
	<T> T second();
}
