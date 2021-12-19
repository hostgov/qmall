package com.qjx.qmall.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * Ryan
 * 2021-10-12-09:37
 */
public class ListedValuesConstrainValidator implements ConstraintValidator<ListedValues, Integer> {


	private Set<Integer> set = new HashSet<>();

	@Override
	public void initialize(ListedValues constraintAnnotation) {
		int[] vals = constraintAnnotation.vals();
		if (vals.length > 0) {
			for (int val : vals) {
				set.add(val);
			}
		}

	}

	@Override
	public boolean isValid(Integer value, ConstraintValidatorContext context) {


		return set.contains(value);
	}
}
