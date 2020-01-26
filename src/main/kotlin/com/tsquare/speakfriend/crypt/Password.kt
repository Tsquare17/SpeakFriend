package com.tsquare.speakfriend.crypt

import org.passay.AllowedCharacterRule.ERROR_CODE
import org.passay.CharacterRule
import org.passay.CharacterData
import org.passay.EnglishCharacterData
import org.passay.PasswordGenerator


class Password {
    var passwordLength: Int = 12
    var numberOfDigits: Int = 2
    var numberOfSymbols: Int = 2

    fun generate(): String? {
        val generator = PasswordGenerator()
        val lowerCaseChars: EnglishCharacterData = EnglishCharacterData.LowerCase
        val lowerCaseRule = CharacterRule(lowerCaseChars)

        val upperCaseChars: EnglishCharacterData = EnglishCharacterData.UpperCase
        val upperCaseRule = CharacterRule(upperCaseChars)

        val digitChars: EnglishCharacterData = EnglishCharacterData.Digit
        val digitRule = CharacterRule(digitChars)
        digitRule.numberOfCharacters = numberOfDigits

        val symbolChars: CharacterData = object : CharacterData {
            override fun getErrorCode(): String {
                return ERROR_CODE
            }
            override fun getCharacters(): String {
                return "!@#$%^&*()_+"
            }
        }
        val symbolRule = CharacterRule(symbolChars)
        symbolRule.numberOfCharacters = numberOfSymbols

        return generator.generatePassword(passwordLength, lowerCaseRule, upperCaseRule, digitRule, symbolRule)
    }
}