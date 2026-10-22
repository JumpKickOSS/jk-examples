package dev.jkexamples.loans;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * A fixed-rate loan repaid monthly: the payment, and what the loan costs in interest. Plain Java,
 * so it is tested without starting JavaFX.
 */
public record Amortization(BigDecimal principal, BigDecimal annualRatePercent, int years) {

    private static final MathContext MC = MathContext.DECIMAL64;

    public Amortization {
        if (principal.signum() <= 0) throw new IllegalArgumentException("the amount must be positive");
        if (annualRatePercent.signum() < 0) throw new IllegalArgumentException("the rate cannot be negative");
        if (years < 1 || years > 50) throw new IllegalArgumentException("the term is 1 to 50 years");
    }

    public int months() {
        return years * 12;
    }

    /** The monthly payment, to the cent: P·r / (1 − (1 + r)^−n) with r the monthly rate. */
    public BigDecimal monthlyPayment() {
        BigDecimal r = annualRatePercent.divide(BigDecimal.valueOf(1200), MC);
        if (r.signum() == 0) return principal.divide(BigDecimal.valueOf(months()), 2, RoundingMode.HALF_UP);
        BigDecimal growth = BigDecimal.ONE.add(r).pow(months(), MC);
        BigDecimal payment = principal.multiply(r).multiply(growth).divide(growth.subtract(BigDecimal.ONE), MC);
        return payment.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal totalInterest() {
        return monthlyPayment().multiply(BigDecimal.valueOf(months())).subtract(principal).setScale(2, RoundingMode.HALF_UP);
    }
}
