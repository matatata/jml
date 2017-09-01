package fl.eval.primitives;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.antlr.runtime.RecognitionException;

import fl.core.Unit;
import fl.eval.Env;
import fl.eval.Evaluator;
import fl.frontend.ast2.IAstNode;
import fl.frontend.parser.Tree2Ast;

public class Importing implements PrimitiveProvider {
	private final Set<String> importedFiles=new HashSet<>();
	
	@Primitive(name="import",syntax=true)
	public Object importFile(Env env,IAstNode _fname) throws FileNotFoundException, RecognitionException, IOException{
		String fname=(String) _fname.getData();
		if(importedFiles.contains(fname)){
			System.err.println("already imported " + fname);
			return Unit.UNIT;
		}
		Object tree = Tree2Ast.getParser(new FileInputStream(new File(fname))).file()
				.getTree();
		
		importedFiles.add(fname);
		
		new Evaluator().visit(Tree2Ast.getAst(tree),env);
		return Unit.UNIT;
	}

	@Override
	public void install(Env env) {
		// nothing to do
	}
}
