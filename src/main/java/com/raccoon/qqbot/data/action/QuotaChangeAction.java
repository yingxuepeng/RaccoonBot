package com.raccoon.qqbot.data.action;

public class QuotaChangeAction extends UserAction {
    public static final Range AdminRange = new Range(-4, 8);
    public static final Range EmperorRange = new Range(-2, 4);
    public static final Range TigerRange = new Range(-1, 2);

    public Range getRange() {
        if (!getSenderPermission().lessThan(Permission.ADMIN)) {
            return AdminRange;
        } else if (getSenderPermission() == Permission.CODING_EMPEROR) {
            return EmperorRange;
        } else if (getSenderPermission() == Permission.CODING_TIGER) {
            return TigerRange;
        }
        return null;
    }

    public int getStep() {
        return 10;
    }

    public static final class Range {
        private int min;
        private int max;

        public Range(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public int getMin() {
            return min;
        }

        public void setMin(int min) {
            this.min = min;
        }

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }
    }
}
