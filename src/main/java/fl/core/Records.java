package fl.core;

import java.util.HashMap;
import java.util.Map;


public class Records {

	public static IRecord recordOf(Map<String, Object> data) {
		if(data==null||data.isEmpty())
			throw new IllegalArgumentException();
		return new Record(data);
	}

	public static IRecord recordOf(String[] keys, Object[] values) {
		if (keys.length != values.length)
			throw new IllegalArgumentException(keys.length + "!="
					+ values.length);
		Map<String, Object> data = new HashMap<>();
		for (int i = 0; i < keys.length; i++) {
			data.put(keys[i], values[i]);
		}
		return recordOf(data);
	}
}
