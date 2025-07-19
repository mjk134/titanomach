package io.github.mjk134.titanomach.utils;

import java.util.List;

public class TextUtils {
    public static String removeFormatting(String text) {
        return text.replaceAll("§[a-f0-9k-or]", "");
    }

    private static final List<String> ROMAN_NUMERALS = List.of("C", "XC", "L", "XL", "X", "IX", "V", "IV", "I");
    private static final List<Integer> ROMAN_NUMERAL_VALUES = List.of(100, 90, 50, 40, 10, 9, 5, 4, 1);
    public static String toRomanNumerals(int num) {
        StringBuilder result = new StringBuilder();
        while (num > 0) {
            for (int i  = 0; i < ROMAN_NUMERAL_VALUES.size(); i++) {
                if (num >= ROMAN_NUMERAL_VALUES.get(i)) {
                    num -= ROMAN_NUMERAL_VALUES.get(i);
                    result.append(ROMAN_NUMERALS.get(i));
                }
            }
        }
        return result.toString();
    }
}
