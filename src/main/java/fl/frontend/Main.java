package fl.frontend;


import java.io.IOException;

import org.antlr.runtime.RecognitionException;

import fl.eval.Env;
import fl.eval.primitives.Arithmetics;
import fl.eval.primitives.Core;
import fl.eval.primitives.Importing;
import fl.eval.primitives.Java;
import fl.eval.primitives.Primitives;
import fl.frontend.ast2.AstConstructor;
import fl.frontend.ast2.AstConstructor.AstNode;
public class Main {

	public static void main(String[] args) throws RecognitionException,
			IOException {
		Env globalEnv = new Env(null);
		Importing imports=new Importing();
		Primitives.installPrimitives(Core.class, new Core(), globalEnv);
		Primitives.installPrimitives(Arithmetics.class, new Arithmetics(),
				globalEnv);
		Primitives.installPrimitives(Importing.class, imports, globalEnv);
		Primitives.installPrimitives(Java.class, new Java(), globalEnv);
		
		imports.importFile(globalEnv, new AstNode(AstConstructor.STRING, "global.fl"));
		
		Env env = new Env(globalEnv);
		try{
		  imports.importFile(env, new AstNode(AstConstructor.STRING, "prelude.fl"));
		}catch(RuntimeException e){
			System.err.println(e.getMessage());
//			e.printStackTrace(System.err);
		}
		
		Repl.repl(env);
	}


}
