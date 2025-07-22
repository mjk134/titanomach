package io.github.mjk134.titanomach.utils;

import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

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

    public static String itemIDtoName(String id) {
        return Registries.ITEM.get(Identifier.of(id)).getName().getString();
    }

    public static String entityIDtoName(String id) {
        return Registries.ENTITY_TYPE.get(Identifier.of(id)).getName().getString();
    }

    /// Generate a progress bar string with formatting. Use `decimalPlaces = -1` to hide the percentage caption.
    public static String progressBar(int length, float percentage, boolean showPercentage) {
        percentage = Float.min(Float.max(percentage, 0.0f), 1.0f);
        int nFilled = (int) (length * percentage);
        String percentageCaption = showPercentage ? " §e" + (int)(percentage * 100)  + "§6%": "";
        return "§2[§a" + "-".repeat(nFilled) + "§7" + "-".repeat(length - nFilled) + "§2]" + percentageCaption;
    }
    public static String progressBarWithOptimisticProgress(int length, float percentage, boolean showPercentage, float optimisticPercentage) {
        percentage = Float.min(Float.max(percentage, 0.0f), 1.0f);
        optimisticPercentage = Float.min(Float.max(optimisticPercentage, 0.0f), 1.0f);
        int npFilled = (int) (length * percentage);
        int nopFilled = (int) (length * optimisticPercentage) - npFilled;
        String percentageCaption = showPercentage ? " §e" + (percentage * 100)  + "§6%": "";
        return "§2[§a" + "-".repeat(npFilled) + "§6" + "-".repeat(nopFilled) + "§7" + "-".repeat(length - npFilled - nopFilled) + "§2]" + percentageCaption;
    }

    public static String capitalize(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}
