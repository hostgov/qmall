package com.qjx.qmall.common.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Ryan
 * 2021-10-12-09:28
 */
@Documented
@Constraint(
		validatedBy = {ListedValuesConstrainValidator.class}
)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ListedValues {
	String message() default "{com.qjx.qmall.common.valid.ListedValues.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	int[] vals() default {};
}
