package fl.frontend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;

import fl.core.ToString;
import fl.eval.Env;
import fl.eval.Evaluator;
import fl.frontend.parser.Tree2Ast;
import fl.types.Environment;
import fl.types.S_T;
import fl.types.TypeInferrer;

public class Repl {

	private static String newLine=System.getProperty("line.separator");
	public Repl() {
	}

	public static void repl(Env env) throws IOException {
		System.out.println("Welcome!");
		System.out.println("");
		
		while (true) {
			try {
				System.out.print("- ");
				Object tree = Tree2Ast
						.getParser(readInput(new InputStreamReader(System.in),System.out))
						.repl().getTree();
//				System.out.println(((CommonTree) tree).toStringTree());
				Object prog = Tree2Ast.getAst(tree);
				
//				typeInferrTest(prog);
				
				Object val = new Evaluator().visit(prog, env);
				env.define("it", val);

				System.out.println("val it = " + ToString.stringify(val));
				
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	private static void typeInferrTest(Object val) {
		try {
			S_T te = new TypeInferrer().visit(val, Environment.EMPTY);
			if (te != null)
				System.out.println(te);
		} catch (Throwable e) {
//			 System.out.println(e.getMessage());
			e.printStackTrace(System.out);
		}
	}

	public static String userRead(InputStream in,PrintStream console) throws IOException {
		return readInput(new InputStreamReader(in),console);
	}

	public static String readInput(Reader in) throws IOException {
		return readInput(in,null);
	}
	public static String readInput(Reader in,PrintStream console) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(in);
		String line = null;
		StringBuilder buf = new StringBuilder();
		boolean multiLine = false;
		while ((line = bufferedReader.readLine()) != null) {
			buf.append(line);
			if (line.trim().equals(";;") || line.trim().endsWith(";;")) {
				break;
			}else  {
				buf.append(newLine);
				multiLine=true;
			}
			if(multiLine)
				console.print(">");
		}
		String input = buf.toString();
		return input;
	}

}
