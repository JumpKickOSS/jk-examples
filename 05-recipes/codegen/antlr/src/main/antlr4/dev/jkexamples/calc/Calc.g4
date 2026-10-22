grammar Calc;

// One statement per line: an assignment or an expression to print.
program   : statement* EOF ;
statement : ID '=' expr NEWLINE?        # assign
          | expr NEWLINE?               # print
          | NEWLINE                     # blank
          ;

expr      : <assoc=right> expr '^' expr               # power
          | '-' expr                                  # negate
          | expr op=('*' | '/' | '%') expr            # multiply
          | expr op=('+' | '-') expr                  # add
          | ID '(' (expr (',' expr)*)? ')'            # call
          | ID                                        # variable
          | NUMBER                                    # number
          | '(' expr ')'                              # parens
          ;

ID      : [a-zA-Z_] [a-zA-Z_0-9]* ;
NUMBER  : [0-9]+ ('.' [0-9]+)? ;
NEWLINE : '\r'? '\n' ;
WS      : [ \t]+ -> skip ;
COMMENT : '#' ~[\r\n]* -> skip ;
