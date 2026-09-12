package com.ambrosia.cluster_controller.model.DTO;

import java.lang.reflect.Method;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EqualsOrMoreValidator implements ConstraintValidator<FieldEqualsOrMore, Object> {
    private String firstField;
    private String secondField;

    @Override
    public void initialize(FieldEqualsOrMore constraintAnnotation) {
        this.firstField = constraintAnnotation.first();
        this.secondField = constraintAnnotation.second();
    }
    
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            Object first = getFieldValue(value, firstField);
            Object second = getFieldValue(value, secondField);
            if (first == null || second == null) {
                return true;
            }
            if (first instanceof Comparable && second.getClass().isAssignableFrom(first.getClass())) {
                @SuppressWarnings("unchecked")
                Comparable<Object> firstComparable = (Comparable<Object>) first;
                return firstComparable.compareTo(second) >= 0;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private Object getFieldValue(Object object, String fieldName) throws Exception {
        Method method = object.getClass().getMethod(fieldName);
        return method.invoke(object);
    }
}
