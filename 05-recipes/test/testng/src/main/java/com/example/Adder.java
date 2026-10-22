package com.example;

/** Adds integers; the thing under test. */
public final class Adder {
    private Adder() {}

    public static int add(int a, int b) {
        return a + b;
    }

    public static int sum(int... values) {
        int total = 0;
        for (int value : values) {
            total = add(total, value);
        }
        return total;
    }

    public static void main(String[] args) {
        int[] values = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            values[i] = Integer.parseInt(args[i]);
        }
        System.out.println(args.length == 0 ? "2 + 2 = " + add(2, 2) : "sum = " + sum(values));
    }
}
