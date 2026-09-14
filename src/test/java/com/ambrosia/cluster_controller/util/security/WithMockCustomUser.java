package com.ambrosia.cluster_controller.util.security;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface  WithMockCustomUser {
    long id() default 1L;
    String username() default "default_test";
    String password() default "default_password";
    String role() default "USER";
}
