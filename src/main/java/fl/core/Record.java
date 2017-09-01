package fl.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


/**
 * 
 * @param <T> for homogenious records
 */
class Record implements IRecord/*, Map<String,Object>*/ {
	private final Map<String,Object> data;
	
	/**
	 * @param data will be shared!
	 */
	protected Record(Map<String,Object> data){
		this.data = data;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T at(String k) {
		if(!data.containsKey(k))
			throw new IllegalArgumentException("no such key " + k);
		return (T) data.get(k);
	}
	
	@Override
	public int length() {
		return data.size();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Iterator iterator() {
		return data.values().iterator();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ITupel map(Function f) {
		Map<String,Object> result=new HashMap<String, Object>();
		for(Entry<String, Object> e:data.entrySet()){
			result.put(e.getKey(), f.apply(e.getValue()));
		}
		return Records.recordOf(result);
	}
	
	@Override
	public Object[] array() {
		return data.values().toArray();
	}
	
	
	@Override
	public String toString() {
		return data.toString();
	}
}
