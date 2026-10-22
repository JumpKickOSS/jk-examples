package dev.jkexamples.loans;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

/** Wires the FXML view to {@link Amortization}: every edit recomputes the payment. */
public class LoanController {

    private static final NumberFormat MONEY = NumberFormat.getCurrencyInstance(Locale.US);

    @FXML private TextField amount;
    @FXML private TextField rate;
    @FXML private Slider years;
    @FXML private Label yearsLabel;
    @FXML private Label payment;
    @FXML private Label interest;

    @FXML
    private void initialize() {
        amount.textProperty().addListener((o, was, now) -> recompute());
        rate.textProperty().addListener((o, was, now) -> recompute());
        years.valueProperty().addListener((o, was, now) -> recompute());
        recompute();
    }

    private void recompute() {
        int term = (int) Math.round(years.getValue());
        yearsLabel.setText(term + " years");
        try {
            var loan = new Amortization(new BigDecimal(amount.getText().trim()), new BigDecimal(rate.getText().trim()), term);
            payment.setText(MONEY.format(loan.monthlyPayment()) + " / month");
            interest.setText(MONEY.format(loan.totalInterest()) + " interest over " + loan.months() + " payments");
        } catch (IllegalArgumentException e) {
            payment.setText("—");
            interest.setText(e instanceof NumberFormatException ? "enter numbers" : e.getMessage());
        }
    }
}
