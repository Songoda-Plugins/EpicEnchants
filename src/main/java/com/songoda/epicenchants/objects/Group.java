package com.songoda.epicenchants.objects;

public class Group {
    private final String identifier;
    private final String name;
    private final String format;
    private final String color;
    private final String descriptionColor;
    private final int slotsUsed;
    private final BookItem bookItem;
    private final int destroyRateMin;
    private final int destroyRateMax;
    private final int successRateMin;
    private final int successRateMax;
    private final int tinkererExp;
    private final int order;

    Group(String identifier, String name, String format, String color, String descriptionColor, int slotsUsed, BookItem bookItem, int destroyRateMin, int destroyRateMax, int successRateMin, int successRateMax, int tinkererExp, int order) {
        this.identifier = identifier;
        this.name = name;
        this.format = format;
        this.color = color;
        this.descriptionColor = descriptionColor;
        this.slotsUsed = slotsUsed;
        this.bookItem = bookItem;
        this.destroyRateMin = destroyRateMin;
        this.destroyRateMax = destroyRateMax;
        this.successRateMin = successRateMin;
        this.successRateMax = successRateMax;
        this.tinkererExp = tinkererExp;
        this.order = order;
    }

    public static GroupBuilder builder() {
        return new GroupBuilder();
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public String getName() {
        return this.name;
    }

    public String getFormat() {
        return this.format;
    }

    public String getColor() {
        return this.color;
    }

    public String getDescriptionColor() {
        return this.descriptionColor;
    }

    public int getSlotsUsed() {
        return this.slotsUsed;
    }

    public BookItem getBookItem() {
        return this.bookItem;
    }

    public int getDestroyRateMin() {
        return this.destroyRateMin;
    }

    public int getDestroyRateMax() {
        return this.destroyRateMax;
    }

    public int getSuccessRateMin() {
        return this.successRateMin;
    }

    public int getSuccessRateMax() {
        return this.successRateMax;
    }

    public int getTinkererExp() {
        return this.tinkererExp;
    }

    public int getOrder() {
        return this.order;
    }

    public static class GroupBuilder {
        private String identifier;
        private String name;
        private String format;
        private String color;
        private String descriptionColor;
        private int slotsUsed;
        private BookItem bookItem;
        private int destroyRateMin;
        private int destroyRateMax;
        private int successRateMin;
        private int successRateMax;
        private int tinkererExp;
        private int order;

        GroupBuilder() {
        }

        public Group.GroupBuilder identifier(String identifier) {
            this.identifier = identifier;
            return this;
        }

        public Group.GroupBuilder name(String name) {
            this.name = name;
            return this;
        }

        public Group.GroupBuilder format(String format) {
            this.format = format;
            return this;
        }

        public Group.GroupBuilder color(String color) {
            this.color = color;
            return this;
        }

        public Group.GroupBuilder descriptionColor(String descriptionColor) {
            this.descriptionColor = descriptionColor;
            return this;
        }

        public Group.GroupBuilder slotsUsed(int slotsUsed) {
            this.slotsUsed = slotsUsed;
            return this;
        }

        public Group.GroupBuilder bookItem(BookItem bookItem) {
            this.bookItem = bookItem;
            return this;
        }

        public Group.GroupBuilder destroyRateMin(int destroyRateMin) {
            this.destroyRateMin = destroyRateMin;
            return this;
        }

        public Group.GroupBuilder destroyRateMax(int destroyRateMax) {
            this.destroyRateMax = destroyRateMax;
            return this;
        }

        public Group.GroupBuilder successRateMin(int successRateMin) {
            this.successRateMin = successRateMin;
            return this;
        }

        public Group.GroupBuilder successRateMax(int successRateMax) {
            this.successRateMax = successRateMax;
            return this;
        }

        public Group.GroupBuilder tinkererExp(int tinkererExp) {
            this.tinkererExp = tinkererExp;
            return this;
        }

        public Group.GroupBuilder order(int order) {
            this.order = order;
            return this;
        }

        public Group build() {
            return new Group(this.identifier, this.name, this.format, this.color, this.descriptionColor, this.slotsUsed, this.bookItem, this.destroyRateMin, this.destroyRateMax, this.successRateMin, this.successRateMax, this.tinkererExp, this.order);
        }

        public String toString() {
            return "Group.GroupBuilder(identifier=" + this.identifier + ", name=" + this.name + ", format=" + this.format + ", color=" + this.color + ", descriptionColor=" + this.descriptionColor + ", slotsUsed=" + this.slotsUsed + ", bookItem=" + this.bookItem + ", destroyRateMin=" + this.destroyRateMin + ", destroyRateMax=" + this.destroyRateMax + ", successRateMin=" + this.successRateMin + ", successRateMax=" + this.successRateMax + ", tinkererExp=" + this.tinkererExp + ", order=" + this.order + ")";
        }
    }
}
