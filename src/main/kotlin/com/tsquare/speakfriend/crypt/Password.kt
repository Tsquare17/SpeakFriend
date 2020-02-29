package com.tsquare.speakfriend.crypt

import org.passay.AllowedCharacterRule.ERROR_CODE
import org.passay.CharacterRule
import org.passay.CharacterData
import org.passay.EnglishCharacterData
import org.passay.PasswordGenerator


class Password {
    var passwordLength: Int = 12
    var numberOfDigits: Int = -1
    var numberOfSymbols: Int = -1
    private var generator: PasswordGenerator = PasswordGenerator()
    private val lowerCaseRule: CharacterRule = CharacterRule(EnglishCharacterData.LowerCase)
    private val upperCaseRule: CharacterRule = CharacterRule(EnglishCharacterData.UpperCase)
    private val digitRule: CharacterRule = CharacterRule(EnglishCharacterData.Digit)
    private var symbolRule: CharacterRule = CharacterRule(EnglishCharacterData.Special)

    fun generate(): String? {
        val symbolChars: CharacterData = object : CharacterData {
            override fun getErrorCode(): String {
                return ERROR_CODE
            }
            override fun getCharacters(): String {
                return "!@#$%^&*()_+"
            }
        }
        symbolRule = CharacterRule(symbolChars)

        if(passwordLength < (numberOfDigits + numberOfSymbols + 2)) {
            passwordLength = numberOfDigits + numberOfSymbols + 2
        }

        if(specifiedDigitsAndSymbols()) {
            return generateWithSpecifiedDigitsAndSymbols()
        }

        if(specifiedDigitsNoSymbols()) {
            return generateWithSpecifiedDigits()
        }

        if(specifiedSymbolsNoDigits()) {
            return generateWithSpecifiedSymbols()
        }

        return generator.generatePassword(passwordLength, lowerCaseRule, upperCaseRule, digitRule, symbolRule)
    }

    private fun specifiedDigitsAndSymbols(): Boolean {
        return numberOfDigits > -1 && numberOfSymbols > -1
    }

    private fun specifiedDigitsNoSymbols(): Boolean {
        return numberOfDigits > -1 && numberOfSymbols == -1
    }

    private fun specifiedSymbolsNoDigits(): Boolean {
        return numberOfDigits == -1 && numberOfSymbols > -1
    }

    // maybe just check if greater than or equal to zero, use rule
    private fun generateWithSpecifiedDigitsAndSymbols(): String {
        // both specified 0
        if(numberOfDigits == 0 && numberOfSymbols == 0) {
            return generator.generatePassword(passwordLength, lowerCaseRule, upperCaseRule)
        }
        // number of digits specified 0, number of symbols specified greater than 0
        if(numberOfDigits == 0) {
            symbolRule.numberOfCharacters = numberOfSymbols

            return generator.generatePassword(passwordLength, lowerCaseRule, upperCaseRule, symbolRule)
        }
        // number of symbols specified 0, number of digits specified greater than 0
        if(numberOfSymbols == 0) {
            digitRule.numberOfCharacters = numberOfDigits

            return generator.generatePassword(passwordLength, lowerCaseRule, upperCaseRule, digitRule)
        }

        digitRule.numberOfCharacters = numberOfDigits
        symbolRule.numberOfCharacters = numberOfSymbols

        return generator.generatePassword(passwordLength, lowerCaseRule, upperCaseRule, digitRule, symbolRule)
    }

    private fun generateWithSpecifiedDigits(): String? {
        if(numberOfDigits == 0) {
            return generator.generatePassword(passwordLength, lowerCaseRule, upperCaseRule, symbolRule)
        }

        digitRule.numberOfCharacters = numberOfDigits

        return  generator.generatePassword(passwordLength, lowerCaseRule, upperCaseRule, digitRule, symbolRule)
    }

    private fun generateWithSpecifiedSymbols(): String? {
        if(numberOfSymbols == 0) {
            return generator.generatePassword(passwordLength, lowerCaseRule, upperCaseRule, digitRule)
        }

        symbolRule.numberOfCharacters = numberOfSymbols

        return generator.generatePassword(passwordLength, lowerCaseRule, upperCaseRule, digitRule, symbolRule)
    }
}