package fl.eval.primitives;

import fl.eval.Env;



public class Arithmetics implements PrimitiveProvider {

	public Arithmetics() {
	}

	@Override
	public void install(Env env) {
		// nothing to do
	}

	@Primitive(alias = ">")
	public static boolean gt(Integer a,Integer b) {
		return a>b;
	}
	@Primitive(alias = ">=")
	public static boolean gte(Integer a,Integer b) {
		return a>=b;
	}
	@Primitive(alias = "<=")
	public static boolean lte(Integer a,Integer b) {
		return a<=b;
	}
	@Primitive(alias = "<")
	public static boolean lt(Integer a,Integer b) {
		return a<b;
	}


	@Primitive(alias = "~")
	public static Integer negatei(Integer arg) {
		return -arg;
	}

	@Primitive(alias = "~.")
	public static Double negater(Double arg) {
		return -arg;
	}

	@Primitive(alias = "+")
	public static Integer addi(Integer x,Integer y) {
		return x+y;
	}

	@Primitive(alias = "-")
	public static Integer subi(Integer x,Integer y) {
		return x-y;
	}

	@Primitive(alias = "*")
	public static Integer muli(Integer x,Integer y) {
		return x*y;
	}

	@Primitive(alias = "/")
	public static Integer divi(Integer x,Integer y) {
		return x/y;
	}

	@Primitive(alias = "+.")
	public static Double addr(Double x, Double y) {
		return x+y;
	}

	@Primitive(alias = "-.")
	public static Double subr(Double x, Double y) {
		return x-y;
	}

	@Primitive(alias = "*.")
	public static Double mulr(Double x, Double y) {
		return x*y;
	}

	@Primitive(alias = "/.")
	public static Double divr(Double x, Double y) {
		return x/y;
	}


}
