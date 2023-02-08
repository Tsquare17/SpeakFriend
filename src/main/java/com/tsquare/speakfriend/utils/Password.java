package com.tsquare.speakfriend.utils;

import static org.passay.AllowedCharacterRule.ERROR_CODE;
import org.passay.CharacterRule;
import org.passay.CharacterData;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

public class Password {
    int passwordLength = 12;
    int numberOfDigits = -1;
    int numberOfSymbols = -1;
    private PasswordGenerator generator = new PasswordGenerator();
    private CharacterRule lowerCaseRule = new CharacterRule(EnglishCharacterData.LowerCase);
    private CharacterRule upperCaseRule = new CharacterRule(EnglishCharacterData.UpperCase);
    private CharacterRule digitRule = new CharacterRule(EnglishCharacterData.Digit);
    private CharacterRule symbolRule = new CharacterRule(EnglishCharacterData.Special);

    public String generate() {
        CharacterData symbolChars = new CharacterData() {
            @Override
            public String getErrorCode() {
                return ERROR_CODE;
            }

            @Override
            public String getCharacters() {
                return "!@#$%^&*()_+";
            }
        };
        symbolRule = new CharacterRule(symbolChars);

        if(passwordLength < (numberOfDigits + numberOfSymbols + 2)) {
            passwordLength = numberOfDigits + numberOfSymbols + 2;
        }

        if(specifiedDigitsAndSymbols()) {
            return generateWithSpecifiedDigitsAndSymbols();
        }

        if(specifiedDigitsNoSymbols()) {
            return generateWithSpecifiedDigits();
        }

        if(specifiedSymbolsNoDigits()) {
            return generateWithSpecifiedSymbols();
        }

        return generator.generatePassword(passwordLength, lowerCaseRule, upperCaseRule, digitRule, symbolRule);
    }

    private boolean specifiedDigitsAndSymbols() {
        return numberOfDigits > -1 && numberOfSymbols > -1;
    }

    private boolean specifiedDigitsNoSymbols() {
        return numberOfDigits > -1 && numberOfSymbols == -1;
    }

    private boolean specifiedSymbolsNoDigits() {
        return numberOfDigits == -1 && numberOfSymbols > -1;
    }

    private String generateWithSpecifiedDigitsAndSymbols() {
        // both specified 0
        if (numberOfDigits == 0 && numberOfSymbols == 0) {
            return generator.generatePassword(passwordLength, lowerCaseRule, upperCaseRule);
        }
        // number of digits specified 0, number of symbols specified greater than 0
        if (numberOfDigits == 0) {
            symbolRule.setNumberOfCharacters(numberOfSymbols);

            return generator.generatePassword(passwordLength, lowerCaseRule, upperCaseRule, symbolRule);
        }
        // number of symbols specified 0, number of digits specified greater than 0
        if (numberOfSymbols == 0) {
            digitRule.setNumberOfCharacters(numberOfDigits);

            return generator.generatePassword(passwordLength, lowerCaseRule, upperCaseRule, digitRule);
        }

        digitRule.setNumberOfCharacters(numberOfDigits);
        symbolRule.setNumberOfCharacters(numberOfSymbols);

        return generator.generatePassword(passwordLength, lowerCaseRule, upperCaseRule, digitRule, symbolRule);
    }

    private String generateWithSpecifiedDigits() {
        if (numberOfDigits == 0) {
            return generator.generatePassword(passwordLength, lowerCaseRule, upperCaseRule, symbolRule);
        }

        digitRule.setNumberOfCharacters(numberOfDigits);

        return  generator.generatePassword(passwordLength, lowerCaseRule, upperCaseRule, digitRule, symbolRule);
    }

    private String generateWithSpecifiedSymbols() {
        if(numberOfSymbols == 0) {
            return generator.generatePassword(passwordLength, lowerCaseRule, upperCaseRule, digitRule);
        }

        symbolRule.setNumberOfCharacters(numberOfSymbols);

        return generator.generatePassword(passwordLength, lowerCaseRule, upperCaseRule, digitRule, symbolRule);
    }

    public void setPasswordLength(int passwordLength) {
        this.passwordLength = passwordLength;
    }

    public void setNumberOfDigits(int numberOfDigits) {
        this.numberOfDigits = numberOfDigits;
    }

    public void setNumberOfSymbols(int numberOfSymbols) {
        this.numberOfSymbols = numberOfSymbols;
    }
}
