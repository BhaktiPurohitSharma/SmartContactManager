package com.smart.helper;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;


/*As you can see, to create an annotation we use the @interface keyword. Let's take a look at a few of the keywords and fully understand them before proceeding:

@Documented: A simple marker annotations which tell whether to add Annotation in Javadocs or not.
@Constraint: Marks an annotation as being a Bean Validation Constraint. The element validatedBy specifies the classes implementing the constraint. We will create the PasswordConstraintValidator class a bit later on.
@Target: Is where our annotations can be used. If you don't specify this, the annotation can be placed anywhere. Currently, our annotation can be placed over an instance variable and on other annotations.
@Retention: Defines for how long the annotation should be kept. We have chosen RUNTIME so that it can be used by the runtime environment.
*/
@Documented
@Constraint(validatedBy = PasswordConstraintValidator.class)
@SupportedValidationTarget(ValidationTarget.PARAMETERS )
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPasswordHelper {

	    String message() default "Invalid Password";

	    Class<?>[] groups() default {};

	    Class<? extends Payload>[] payload() default {};
	
}
