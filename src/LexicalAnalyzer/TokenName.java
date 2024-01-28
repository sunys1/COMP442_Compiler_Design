package LexicalAnalyzer;

/**
*
* @Description COMP442 A1
* @author Yizhou Sun 40056775
* @date Jan 28, 2024
*/

public enum TokenName {
	ID, INTNUM, FLOATNUM,
    PLUS, MINUS, DIV,  DOT, COMMA, SEMI, COLON, COLONCOLON, ARROW, AND, MULT, OR,
    OPENPAR, OPENCUBR, OPENSQBR,
    CLOSEPAR, CLOSECUBR, CLOSESQBR,
    ASSIGN, EQ, GT, GEQ, LT, LEQ, NOTEQ,
    INLINECMT, BLOCKCMT,
    ERROR,
    IF, THEN, ELSE, INTEGER, FLOAT, VOID,
    PUBLIC, PRIVATE, FUNC, VAR, STRUCT, WHILE,
    READ, WRITE, RETURN, SELF, INHERITS, LET, IMPL
}
