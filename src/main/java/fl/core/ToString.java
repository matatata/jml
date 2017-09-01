package fl.core;

import java.util.Iterator;

import fl.eval.types.TypeAbbreviation;

@SuppressWarnings("rawtypes")
public class ToString {

	public static String asString(Object e){
		if(e instanceof String){
			return (String)e;
		}
		return stringify(e);
	}
	
	public static String stringify(Object e){
		if(e == Lists.nil())
			return "[]";
		if(e instanceof String){
			return "\"" + e + "\"";
		}
//		if(e instanceof Function){
//			return "_fn";
//		}
		if(e instanceof Number){
			String asString = String.valueOf(e);
			String tilde = e instanceof Double ? "~." : "~";
			return asString.charAt(0)=='-'? tilde  + asString.substring(1) : asString;
		}
		return String.valueOf(e);
	}
	
	private static String stringifyProduct(ILst pte){
		StringBuilder buf=new StringBuilder("(");
		Iterator it = pte.iterator();
		while(true){
			buf.append(stringifyTypeExp(it.next()));
			if(!it.hasNext())
				break;
			buf.append(" * ");
		}
		
		return buf.append(")").toString();
	}
	
	public static String stringifyTypeExp(Object te){
		if(te == null) //empty product ()
			return stringify(te);
		if(te instanceof ILst){
			return stringifyProduct((ILst) te);
		}
		if (te instanceof TypeAbbreviation)
			return ((TypeAbbreviation) te).getName();
		return te.toString();
	}
	
	public static String stringifyLst(ILst list) {
		StringBuilder buf = new StringBuilder("[");
		ILst l = list;
		while (l != Lists.nil()) {
			buf.append(stringify(l.head()));
			l = l.tail();
			if (l != Lists.nil())
				buf.append(", ");
		}
		buf.append("]");
		return buf.toString();
	}
	
	public static <K,T> String stringifyTupel(ITupel tup) {
		StringBuilder buf=new StringBuilder("(");
		for(Iterator it=tup.iterator();it.hasNext();){
			Object elem = it.next();
			buf.append(stringify(elem));
			if(it.hasNext())
				buf.append(", ");
		}
		buf.append(")");
		return buf.toString();
	}
	
}
