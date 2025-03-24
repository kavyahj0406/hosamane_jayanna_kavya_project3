
public class ParserImpl extends Parser {

    /*
     * Implements a recursive-descent parser for the following CFG:
     * 
     * T -> F AddOp T              { if ($2.type == TokenType.PLUS) { $$ = new PlusExpr($1,$3); } else { $$ = new MinusExpr($1, $3); } }
     * T -> F                      { $$ = $1; }
     * F -> Lit MulOp F            { if ($2.type == TokenType.Times) { $$ = new TimesExpr($1,$3); } else { $$ = new DivExpr($1, $3); } }
     * F -> Lit                    { $$ = $1; }
     * Lit -> NUM                  { $$ = new FloatExpr(Float.parseFloat($1.lexeme)); }
     * Lit -> LPAREN T RPAREN      { $$ = $2; }
     * AddOp -> PLUS               { $$ = $1; }
     * AddOp -> MINUS              { $$ = $1; }
     * MulOp -> TIMES              { $$ = $1; }
     * MulOp -> DIV                { $$ = $1; }
     */
    @Override
    public Expr do_parse() throws Exception {
        return parseT();
    }
    private Expr parseT() throws Exception {
        Expr left = parseF();
        
        // Check for AddOp (PLUS or MINUS)
        if (peek(TokenType.PLUS,0) || peek(TokenType.MINUS,0)) {
            Token op = consumeAddOp();
            Expr right = parseT();
            
            // Create appropriate expression based on operator
            if (op.ty == TokenType.PLUS) {
                return new PlusExpr(left, right);
            } else {
                return new MinusExpr(left, right);
            }
        }
        return left;
    }

    private Expr parseF() throws Exception {
        Expr left = parseLit();
        
        // Check for MulOp (TIMES or DIV)
        if (peek(TokenType.TIMES,0) || peek(TokenType.DIV,0)) {
            Token op = consumeMulOp();
            Expr right = parseF();
            
            // Create appropriate expression based on operator
            if (op.ty == TokenType.TIMES) {
                return new TimesExpr(left, right);
            } else {
                return new DivExpr(left, right);
            }
        }
        return left;
    }

    private Expr parseLit() throws Exception {
        if (peek(TokenType.NUM,0)) {
            // Handle numeric literals
            Token numToken = consume(TokenType.NUM);
            return new FloatExpr(Float.parseFloat(numToken.lexeme));
        } 
        else if (peek(TokenType.LPAREN,0)) {
            // Handle parenthesized expressions
            consume(TokenType.LPAREN);
            Expr expr = parseT();
            consume(TokenType.RPAREN);
            return expr;
        }
        else {
            throw new Exception("Expected number or parenthesis, found: " + 
                              (tokens != null ? tokens.elem.ty : "EOF"));
        }
    }

    private Token consumeAddOp() throws Exception {
        if (peek(TokenType.PLUS,0)) {
            return consume(TokenType.PLUS);
        } else if (peek(TokenType.MINUS,0)) {
            return consume(TokenType.MINUS);
        }
        throw new Exception("Expected '+' or '-' operator");
    }

    private Token consumeMulOp() throws Exception {
        if (peek(TokenType.TIMES,0)) {
            return consume(TokenType.TIMES);
        } else if (peek(TokenType.DIV,0)) {
            return consume(TokenType.DIV);
        }
        throw new Exception("Expected '*' or '/' operator");
    }


}
