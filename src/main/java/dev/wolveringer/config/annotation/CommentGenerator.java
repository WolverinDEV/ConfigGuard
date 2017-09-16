package dev.wolveringer.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by wolverindev on 10.09.17.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface CommentGenerator {
    Class generatorClass();

    /**
     * Parameter layout:
     *  - \\<methodeName\\>(<class>java.lang.Object</class> value)
     *  - \\<methodeName\\>(<class>java.lang.reflect.Field</class> field)
     *  - \\<methodeName\\>(<class>java.lang.reflect.Field</class> field, <class>java.lang.Object</class> value)
     *  - \\<methodeName\\>(<class>java.lang.reflect.Field</class> field, <class>java.lang.Object</class> value, <class>java.lang.Object</class> annonationInstance)
     * @return
     */
    String methode();
}
