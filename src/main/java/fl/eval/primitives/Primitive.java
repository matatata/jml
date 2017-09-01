package fl.eval.primitives;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Primitive {
	String name() default "";
	String alias() default "";
	boolean syntax() default false;
}
