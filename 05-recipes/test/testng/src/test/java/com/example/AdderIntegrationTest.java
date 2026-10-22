package com.example;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

/** A TestNG group is a Platform tag: this class is out of the unit tier and in `--profile integration`. */
@Test(groups = "integration")
public class AdderIntegrationTest {

    public void addsAcrossTheIntRange() {
        assertEquals(Adder.add(Integer.MAX_VALUE, 1), Integer.MIN_VALUE);
    }
}
