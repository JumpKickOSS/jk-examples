package dev.jkexamples.calc;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Walks a parsed program with the generated visitor: variables persist between statements. */
public final class Evaluator extends CalcBaseVisitor<BigDecimal> {

    private static final MathContext MATH = MathContext.DECIMAL64;

    private final Map<String, BigDecimal> variables = new LinkedHashMap<>(Map.of("pi", new BigDecimal(Math.PI, MATH)));
    private final List<String> printed = new ArrayList<>();

    /** What the program printed, one line per expression statement. */
    public List<String> printed() {
        return List.copyOf(printed);
    }

    @Override
    public BigDecimal visitAssign(CalcParser.AssignContext ctx) {
        BigDecimal value = visit(ctx.expr());
        variables.put(ctx.ID().getText(), value);
        return value;
    }

    @Override
    public BigDecimal visitPrint(CalcParser.PrintContext ctx) {
        BigDecimal value = visit(ctx.expr());
        printed.add(value.stripTrailingZeros().toPlainString());
        return value;
    }

    @Override
    public BigDecimal visitPower(CalcParser.PowerContext ctx) {
        BigDecimal exponent = visit(ctx.expr(1));
        if (exponent.stripTrailingZeros().scale() > 0) throw new CalcException("only whole exponents: " + ctx.getText());
        return visit(ctx.expr(0)).pow(exponent.intValueExact(), MATH);
    }

    @Override
    public BigDecimal visitNegate(CalcParser.NegateContext ctx) {
        return visit(ctx.expr()).negate();
    }

    @Override
    public BigDecimal visitMultiply(CalcParser.MultiplyContext ctx) {
        BigDecimal left = visit(ctx.expr(0));
        BigDecimal right = visit(ctx.expr(1));
        return switch (ctx.op.getText()) {
            case "*" -> left.multiply(right, MATH);
            case "/" -> {
                if (right.signum() == 0) throw new CalcException("division by zero: " + ctx.getText());
                yield left.divide(right, MATH);
            }
            default -> left.remainder(right, MATH);
        };
    }

    @Override
    public BigDecimal visitAdd(CalcParser.AddContext ctx) {
        BigDecimal left = visit(ctx.expr(0));
        BigDecimal right = visit(ctx.expr(1));
        return ctx.op.getText().equals("+") ? left.add(right, MATH) : left.subtract(right, MATH);
    }

    @Override
    public BigDecimal visitCall(CalcParser.CallContext ctx) {
        List<BigDecimal> args = ctx.expr().stream().map(this::visit).toList();
        String name = ctx.ID().getText();
        return switch (name) {
            case "min" -> args.stream().reduce(BigDecimal::min).orElseThrow(() -> new CalcException("min() needs arguments"));
            case "max" -> args.stream().reduce(BigDecimal::max).orElseThrow(() -> new CalcException("max() needs arguments"));
            case "sqrt" -> {
                if (args.size() != 1) throw new CalcException("sqrt takes one argument");
                yield args.getFirst().sqrt(MATH);
            }
            default -> throw new CalcException("unknown function " + name + "()");
        };
    }

    @Override
    public BigDecimal visitVariable(CalcParser.VariableContext ctx) {
        BigDecimal value = variables.get(ctx.ID().getText());
        if (value == null) throw new CalcException("undefined variable " + ctx.ID().getText());
        return value;
    }

    @Override
    public BigDecimal visitNumber(CalcParser.NumberContext ctx) {
        return new BigDecimal(ctx.NUMBER().getText());
    }

    @Override
    public BigDecimal visitParens(CalcParser.ParensContext ctx) {
        return visit(ctx.expr());
    }
}
