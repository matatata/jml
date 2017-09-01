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
import jline.TerminalFactory;
import jline.console.ConsoleReader;

/**
 * Read-Eval-Print Loop
 */
public class Repl {

	private static String newLine=System.getProperty("line.separator");


	public void repl(Env env) throws IOException {
		
		installShutdownHook();
		
		System.out.println("Welcome to JML!\n\n\";\" separates expressions, whereas \";;\" indicates the end of the input\n" + 
				"and triggers it's evaluation");
		System.out.println("");

		ConsoleReader console = new ConsoleReader();
		console.setPrompt(">");
		
		
		while (true) {
			try {
				String input = 
						readJLineInput(console,System.out);
				
				Object tree = Tree2Ast
						.getParser(input)
						.repl().getTree();
				Object prog = Tree2Ast.getAst(tree);
				
				Object val = new Evaluator().visit(prog, env);
				env.define("it", val);

				System.out.println("val it = " + ToString.stringify(val));
				
			} catch (Throwable e) {
				System.err.println(e.getMessage());
			}
		}
		
	}

	private void installShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread()
	     {
	         @Override
	         public void run()
	         {
	             cleanup();
	         }
	     });
	}



	public void cleanup() {
		try {
		    TerminalFactory.get().restore();
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}


	public String userRead(InputStream in,PrintStream console) throws IOException {
		return readInput(new InputStreamReader(in),console);
	}

	public String readInput(Reader in) throws IOException {
		return readInput(in,null);
	}
	
	public String readInput(Reader in,PrintStream outputConsole) throws IOException {
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
			if(multiLine && outputConsole!=null)
				outputConsole.print(">");
		}
		return buf.toString();
	}
	
	/**
	 * Reading input using JLine that is similar to GNU's readline lib 
	 * @param console
	 * @param outputConsole
	 */
	public String readJLineInput(ConsoleReader console,PrintStream outputConsole) throws IOException {
		String line = null;
		StringBuilder buf = new StringBuilder();
		while ((line = console.readLine()) != null) {
			buf.append(line);
			if (line.trim().endsWith(";;")) {
				break;
			}else  {
				buf.append(newLine);
			}
		}
		return buf.toString();
	}


}
