package fl.frontend;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.antlr.runtime.RecognitionException;

import fl.eval.Env;
import fl.eval.primitives.Arithmetics;
import fl.eval.primitives.Core;
import fl.eval.primitives.Importing;
import fl.eval.primitives.Java;
import fl.eval.primitives.Primitives;

public class JML {

	private Env globalEnv;
	
	public JML() throws RecognitionException, IOException {
		globalEnv = new Env(null);
		Primitives.installPrimitives(Core.class, new Core(), globalEnv);
		Primitives.installPrimitives(Arithmetics.class, new Arithmetics(),
				globalEnv);
		Primitives.installPrimitives(Importing.class, new Importing(), globalEnv);
		Primitives.installPrimitives(Java.class, new Java(), globalEnv);
		
		Importing.evaluateFromInputStream(globalEnv,
				ClassLoader.getSystemResourceAsStream("global.fl"));
	}

	public static void main(String[] args) throws RecognitionException,
			IOException {
		new JML().interactive(args);
	}
	
	public Env getGlobalEnv() {
		return globalEnv;
	}

	
	public Object executeFile(Env env,String filename) throws FileNotFoundException, RecognitionException, IOException {
		return Importing.evaluateFromInputStream(env, new FileInputStream(new File(filename)));
	}
	
	public void interactive(String[] args) throws RecognitionException, IOException, FileNotFoundException {
		Env env = new Env(getGlobalEnv());
		
		for(int i=0;i<args.length;i++) {
			executeFile(env, args[i]);
		}
		
		Repl.repl(env);
	}


}
