package com.example;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class AdderTest {

    @Test
    public void adds() {
        assertEquals(Adder.add(2, 2), 4);
    }

    @Test
    public void sumsNothingToZero() {
        assertEquals(Adder.sum(), 0);
    }

    @DataProvider
    public Object[][] sums() {
        return new Object[][] {{new int[] {1, 2, 3}, 6}, {new int[] {-1, 1}, 0}, {new int[] {7}, 7}};
    }

    @Test(dataProvider = "sums")
    public void sumsEveryValue(int[] values, int expected) {
        assertEquals(Adder.sum(values), expected);
    }
}
