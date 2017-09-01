package fl.eval.primitives;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.antlr.runtime.RecognitionException;

import fl.core.Unit;
import fl.eval.Env;
import fl.eval.Evaluator;
import fl.frontend.ast2.IAstNode;
import fl.frontend.parser.Tree2Ast;

public class Importing implements PrimitiveProvider {
	private final Set<String> importedFiles = new HashSet<>();

	@Primitive(name = "import", syntax = true)
	public Object importFile(Env env, IAstNode _fname) throws FileNotFoundException, RecognitionException, IOException {
		String fname = (String) _fname.getData();
		if (importedFiles.contains(fname)) {
			System.err.println("already imported " + fname);
			return Unit.UNIT;
		}

		try (FileInputStream fis = new FileInputStream(new File(fname))) {
			evaluateFromInputStream(env, fis);
		}

		importedFiles.add(fname);
		
		return Unit.UNIT;
	}



	public static Object evaluateFromInputStream(Env env, InputStream fis) throws RecognitionException, IOException {
		Object tree = Tree2Ast.getParser(fis).file().getTree();
		return new Evaluator().visit(Tree2Ast.getAst(tree), env);
	}
	
	

	@Override
	public void install(Env env) {
		// nothing to do
	}
}
