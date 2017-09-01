package fl.eval;

import static fl.core.ToString.stringify;
import static fl.core.ToString.stringifyTypeExp;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Env {
	private final Map<String,Object> bindings=new HashMap<String, Object>();
	private final Map<String,Object> typedefs=new HashMap<String, Object>();
	private final Env parent;
	
	public Env(Env parent){
		this.parent = parent;
	}
	
	/**
	 * 
	 * @param symbol
	 * @param val
	 * @return this for chaining
	 */
	public Env define(String symbol, Object val) {
		if("_".equals(symbol))
			return this;
		bindings.put(symbol, val);
		return this;
	}
	
	public Env addTypeDef(String symbol, Object tval){
		typedefs.put(symbol, tval);
		return this;
	}
	
	public Object lookupTypeDef(String symbol){
		if(typedefs.containsKey(symbol))
			return typedefs.get(symbol);
		if(parent!=null)
			return parent.lookupTypeDef(symbol);
		throw new NotBoundException(symbol);
	}
	
	public Object lookup(String symbol){
		if(bindings.containsKey(symbol))
			return bindings.get(symbol);
		if(parent!=null)
			return parent.lookup(symbol);
		throw new NotBoundException(symbol);
	}
	
	
	@Override
	public String toString() {
		StringBuilder buf=new StringBuilder();
		Iterator<Entry<String, Object>> it = bindings.entrySet().iterator();
		while(it.hasNext()){
			Entry<String, Object> e = it.next();
			buf.append(e.getKey() + "=" + stringify(e.getValue()));
			if(it.hasNext())
				buf.append("\n");
		}
		it = typedefs.entrySet().iterator();
		if(it.hasNext())
			buf.append("\nTypeDefs:\n");
		while(it.hasNext()){
			Entry<String, Object> e = it.next();
			buf.append(e.getKey() + "=" + stringifyTypeExp(e.getValue()));
			if(it.hasNext())
				buf.append("\n");
		}
		return buf.toString();
	}

	public void dump(PrintStream out) {
		Env env = this;
		while(env!=null){
			out.println(env);
			env = env.parent;
		}
	}
}
