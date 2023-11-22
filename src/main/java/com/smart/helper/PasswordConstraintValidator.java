package com.smart.helper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPasswordHelper, String> {

    @Override
    public void initialize(ValidPasswordHelper arg0) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
    	 PasswordValidator validator = new  PasswordValidator(Arrays.asList(
            // at least 8 characters
            new LengthRule(8, 500),

            // at least one upper-case character
            new CharacterRule(EnglishCharacterData.UpperCase, 1),

            // at least one lower-case character
            new CharacterRule(EnglishCharacterData.LowerCase, 1),

            // at least one digit character
            new CharacterRule(EnglishCharacterData.Digit, 1),

            // at least one symbol (special character)
            new CharacterRule(EnglishCharacterData.Special, 1),

            // no whitespace
            new WhitespaceRule()

        ));
        RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }
        List<String> messages = validator.getMessages(result);

        String messageTemplate = messages.stream()
            .collect(Collectors.joining(","));
        context.buildConstraintViolationWithTemplate(messageTemplate)
            .addConstraintViolation()
            .disableDefaultConstraintViolation();
        return false;
    }
}

//We first created a PasswordValidator object by passing an array of constraints that we want to enforce in our password.
//
//The constraints are self-explanatory:
//
//It must be between 8 and 30 characters long as defined by the LengthRule
//It must have at least 1 lowercase character as defined by the CharacterRule
//It must have at least 1 uppercase character as defined by the CharacterRule
//It must have at least 1 special character as defined by the CharacterRule
//It must have at least 1 digit character as defined by the CharacterRule
//It must not contain whitespaces as defined by the WhitespaceRule
//The full list of rules that can be written using Passay can be found on the official website.