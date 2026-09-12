package com.ambrosia.cluster_controller.model.DTO;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = EqualsOrMoreValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldEqualsOrMore {
    String message() default "Fields doesn't valid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String first();
    String second();
}
