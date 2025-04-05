package com.songoda.epicenchants.objects;

public class Placeholder {
    private final String placeholder;
    private final Object toReplace;

    private Placeholder(String placeholder, Object toReplace) {
        this.placeholder = "{" + placeholder + "}";
        this.toReplace = toReplace;
    }

    public static Placeholder of(String placeholder, Object toReplace) {
        return new Placeholder(placeholder, toReplace);
    }

    public String getPlaceholder() {
        return this.placeholder;
    }

    public Object getToReplace() {
        return this.toReplace;
    }
}
