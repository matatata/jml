package fl.core;



@SuppressWarnings("rawtypes")
public interface ILst extends Iterable, IBinaTupel{
	<T> T head();
	ILst tail();
//	Object[] toArray();
	boolean isNil();
	@Override
	ILst map(Function f);
}