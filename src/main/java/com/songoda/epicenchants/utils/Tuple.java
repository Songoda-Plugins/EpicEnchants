package com.songoda.epicenchants.utils;

public class Tuple<key, value> {
    private final key x;
    private final value y;

    public Tuple(key x, value y) {
        this.x = x;
        this.y = y;
    }

    public key getLeft() {
        return this.x;
    }

    public value getRight() {
        return this.y;
    }

    public static <key, value> Tuple of(key x, value y) {
        return new Tuple<>(x, y);
    }
}
