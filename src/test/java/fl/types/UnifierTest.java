package fl.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import fl.core.Function;
import fl.frontend.tast.Arrow;
import fl.frontend.tast.TypeVariable;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class UnifierTest {

	@Test
	public void testReplace() {
		Object exp = new Arrow("int", fl.core.Lists.of(new TypeVariable("x"), "'b"));
		Object replaced = Substitution.replace("'b", "foo").apply(exp);
		assertEquals("(int->(x * foo))", replaced.toString());
	}

	@Test
	public void testUnifyVar() {
		Function r = Unifier.unify(new TypeVariable("int"), "'a");
		assertEquals(new TypeVariable("int"), r.apply("'a"));
		r = Unifier.unify("'a", new TypeVariable("int"));
		assertEquals(new TypeVariable("int"), r.apply("'a"));
	}

	@Test
	public void testUnifyVarCirc() {
		Function r = Unifier
				.unify(new Arrow(new TypeVariable("int"), "'a"), "'a");
		try {
			r.apply("'a");
			fail();
		} catch (CircularityException e) {
		}
	}
	
	@Test
	public void testUnifyFn() {
		Function r = Unifier
				.unify(new Arrow(new TypeVariable("double"), "'a"), new Arrow("'b",new TypeVariable("int")));
		
		assertEquals(fl.core.Lists.of(new TypeVariable("double"), new TypeVariable("int")),
				r.apply(fl.core.Lists.of("'b", "'a")));
	}
	
	@Test
	public void testUnifyProduct() {
		Function r = Unifier
				.unify(fl.core.Lists.of(new TypeVariable("double"), "'a"), fl.core.Lists.of("'b",new TypeVariable("int")));
		assertEquals(fl.core.Lists.of(new TypeVariable("double"), new TypeVariable("int")),
				r.apply(fl.core.Lists.of("'b", "'a")));
	}
	
	@Test
	public void testUnifyProductWithFn() {
		Function r = Unifier
				.unify(fl.core.Lists.of(new Arrow(new TypeVariable("double"),"'c"), "'a"), fl.core.Lists.of(new Arrow("'b","'c"),new TypeVariable("int")));
		assertEquals(
				fl.core.Lists.of(new TypeVariable("double"), new TypeVariable("int"), "'c"),
				r.apply(fl.core.Lists.of("'b", "'a", "'c")));
	}

	@Test
	public void testOccurs() {
		Object exp = new Arrow("int", fl.core.Lists.of(new TypeVariable("x"), "'b"));
		assertTrue(Unifier.occurs("'b", exp));
		assertFalse(Unifier.occurs("'c", exp));
	}
}
