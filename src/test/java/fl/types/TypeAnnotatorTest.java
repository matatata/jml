package fl.types;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import fl.eval.Env;
import fl.frontend.ast.AstVisitor;
import fl.frontend.ast.Symbol;

@Ignore
public class TypeAnnotatorTest {

  private TypeAnnotator typeAnnotator;
  private Env env;

  @Before
  public void before() {

    typeAnnotator = new TypeAnnotator();
    env = new Env(null);

  }

  @Test
  public void testc() {

    assertEquals(AstVisitor.newTa(1, "Integer"), typeAnnotator.visit(1, env));

  }

  @Test
  public void testsy() {

    assertEquals(AstVisitor.newTa(new Symbol("x"), "'0"), typeAnnotator.visit(new Symbol("x"), env));

  }

  @Test
  public void testif() {

    assertEquals(AstVisitor.newTa(AstVisitor.newIf(AstVisitor.newTa(new Symbol("x"), "'0"),
        AstVisitor.newTa(1, "Integer"), AstVisitor.newTa(2, "Integer")), "'1"),
        typeAnnotator.visit(AstVisitor.newIf(new Symbol("x"), 1, 2), env));

  }

}
