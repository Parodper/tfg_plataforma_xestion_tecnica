grammar Find;

//Lexer
PROPERTY: [a-z]+ ('.' [a-z]+)* ;

STRING: '"' (~[\\] | '\\' '"' )*? '"' ;
STRING_NOT_MATCH: '!~' ;
STRING_MATCH: '~' ;

DIGIT: [0-9] ;
NUMBER: [+-]? DIGIT+ ('.' DIGIT+)? ;
NUMBER_GT_EQ: '>=' ;
NUMBER_LT_EQ: '<=' ;
NUMBER_NEQ: '!='   ;
NUMBER_LT: '<' ;
NUMBER_GT: '>' ;
NUMBER_EQ: '=' ;

DATETIME:  DIGIT DIGIT DIGIT DIGIT '-' DIGIT DIGIT '-' DIGIT DIGIT 'T' DIGIT DIGIT ':' DIGIT DIGIT ':' DIGIT DIGIT ;

TUPLE_IN: '@' ;
TUPLE_NOT_IN: '!@' ;

WS: ( ' ' | '\n' | '\r' | '\t' )+ -> skip ;
L_PAREN: '(' ;
R_PAREN: ')' ;
L_SQUARE: '[' ;
R_SQUARE: ']' ;
AND: '&' ;
OR: '|' ;

//Parser
top: expresion EOF ; // EOF needed thanks to issue #118 on antlr/antlr4's GitHub

expresion: test # ExpBase
	| L_PAREN expresion R_PAREN # ExpComplex
	| expresion AND expresion # ExpAnd
	| expresion OR expresion # ExpOr ;

test: PROPERTY (STRING_NOT_MATCH | STRING_MATCH) STRING # TestString
	| PROPERTY (NUMBER_GT_EQ | NUMBER_LT_EQ | NUMBER_NEQ | NUMBER_GT | NUMBER_LT | NUMBER_EQ) (NUMBER | DATETIME) # TestNum
	| PROPERTY (TUPLE_IN | TUPLE_NOT_IN) L_SQUARE STRING (',' STRING)*? R_SQUARE  # TestIn;

