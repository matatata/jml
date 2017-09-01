package fl.eval;

import java.io.IOException;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import fl.eval.primitives.Importing;
import fl.frontend.JML;

public class RegressionTest {

	@Test
	public void testRegressionTestFile() throws RecognitionException, IOException {
		Importing.evaluateFromInputStream(new JML().getGlobalEnv(),
				ClassLoader.getSystemResourceAsStream("test.fl"));
	}

}
