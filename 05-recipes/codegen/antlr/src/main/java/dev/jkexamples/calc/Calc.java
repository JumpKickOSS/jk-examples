package dev.jkexamples.calc;

import java.util.List;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

/** Parse and run a program; a syntax error is a {@link CalcException} naming the line and column. */
public final class Calc {

    private Calc() {}

    public static List<String> run(String program) {
        CalcLexer lexer = new CalcLexer(CharStreams.fromString(program.endsWith("\n") ? program : program + "\n"));
        CalcParser parser = new CalcParser(new CommonTokenStream(lexer));
        var failFast = new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> r, Object symbol, int line, int col, String msg, RecognitionException e) {
                throw new CalcException("line " + line + ":" + col + " " + msg);
            }
        };
        lexer.removeErrorListeners();
        lexer.addErrorListener(failFast);
        parser.removeErrorListeners();
        parser.addErrorListener(failFast);
        Evaluator evaluator = new Evaluator();
        evaluator.visit(parser.program());
        return evaluator.printed();
    }
}
