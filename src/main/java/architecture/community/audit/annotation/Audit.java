package architecture.community.audit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Audit {

    /**
     * Identifier for this particular application in the audit trail logs.  This attribute should only be used to override the basic application code when you want to differentiate a section of the code.
     * @return the application code or an empty String if none is set.
     */
    String applicationCode() default "";

    /**
     * The action to write to the log when we audit this method.  Value must be defined.
     * @return the action to write to the logs.
     */
    String action();

    /**
     * Reference name of the resource resolver to use.
     *
     * @return the reference to the resource resolver.  CANNOT be NULL.
     */
    String resourceResolverName();

    /**
     * Reference name of the action resolver to use.
     *
     * @return the reference to the action resolver.  CANNOT be NULL.
     */
    String actionResolverName();
}
