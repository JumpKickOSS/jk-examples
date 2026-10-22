package dev.jkexamples.calc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class CalcTest {

    @Test
    void precedence_and_right_associative_power() {
        assertEquals(List.of("14", "512", "-9"), Calc.run("2 + 3 * 4\n2 ^ 3 ^ 2\n-3 ^ 2\n"));
    }

    @Test
    void variables_and_functions() {
        assertEquals(List.of("12", "3"), Calc.run("rate = 1.2\nnights = 10\nrate * nights\nsqrt(max(4, 9, 1))\n"));
    }

    @Test
    void errors_name_where_they_happened() {
        assertTrue(assertThrows(CalcException.class, () -> Calc.run("1 +\n")).getMessage().startsWith("line "));
        assertEquals("undefined variable x", assertThrows(CalcException.class, () -> Calc.run("x * 2")).getMessage());
        assertEquals("division by zero: 1/0", assertThrows(CalcException.class, () -> Calc.run("1/0")).getMessage());
    }
}
