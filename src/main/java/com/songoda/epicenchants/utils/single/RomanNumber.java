package com.songoda.epicenchants.utils.single;

import java.util.TreeMap;

public class RomanNumber {
    private static final TreeMap<Integer, String> TREE_MAP = new TreeMap<Integer, String>() {{
        put(1000, "M");
        put(900, "CM");
        put(500, "D");
        put(400, "CD");
        put(100, "C");
        put(90, "XC");
        put(50, "L");
        put(40, "XL");
        put(10, "X");
        put(9, "IX");
        put(5, "V");
        put(4, "IV");
        put(1, "I");
        put(-1, "-1");
    }};

    public static String toRoman(int number) {
        int l = TREE_MAP.floorKey(number);
        if (number == l) {
            return TREE_MAP.get(number);
        }
        return TREE_MAP.get(l) + toRoman(number - l);
    }
}
