package fl.core;

public interface IRecord extends ITupel{
	<T> T at(String key);
}
