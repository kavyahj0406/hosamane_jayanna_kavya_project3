public class CompilerFrontendImpl extends CompilerFrontend {
    public CompilerFrontendImpl() {
        super();
    }

    public CompilerFrontendImpl(boolean debug_) {
        super(debug_);
    }

    /*
     * Initializes the local field "lex" to be equal to the desired lexer.
     * The desired lexer has the following specification:
     * 
     * NUM: [0-9]*\.[0-9]+
     * PLUS: \+
     * MINUS: -
     * TIMES: \*
     * DIV: /
     * WHITE_SPACE (' '|\n|\r|\t)*
     */
    @Override
    protected void init_lexer() {
        LexerImpl lexer = new LexerImpl();
    
        // Create and add automata for each token type
        lexer.add_automaton(TokenType.NUM, createNumNfa());
        lexer.add_automaton(TokenType.PLUS, createSingleCharNfa('+'));
        lexer.add_automaton(TokenType.MINUS, createSingleCharNfa('-'));
        lexer.add_automaton(TokenType.TIMES, createSingleCharNfa('*'));
        lexer.add_automaton(TokenType.DIV, createSingleCharNfa('/'));
        lexer.add_automaton(TokenType.LPAREN, createSingleCharNfa('('));
        lexer.add_automaton(TokenType.RPAREN, createSingleCharNfa(')'));
        lexer.add_automaton(TokenType.WHITE_SPACE, createWhitespaceNfa());
        
        this.lex = lexer;
    }
    private Automaton createNumNfa() {
        Automaton nfa = new AutomatonImpl();
        
        // States
        nfa.addState(0, true, false);  // Start state
        nfa.addState(1, false, false); // After decimal point
        nfa.addState(2, false, true);  // Accept state (has digits after decimal)
        
        // Transitions for digits 0-9
        for (char c = '0'; c <= '9'; c++) {
            nfa.addTransition(0, c, 0);  // [0-9]*
            nfa.addTransition(1, c, 2);  // First digit after decimal
            nfa.addTransition(2, c, 2);  // Subsequent digits
        }
        
        // Decimal point transition
        nfa.addTransition(0, '.', 1);
        
        return nfa;
    }
    
    // Helper method for single-character tokens
    private Automaton createSingleCharNfa(char c) {
        Automaton nfa = new AutomatonImpl();
        nfa.addState(0, true, false);
        nfa.addState(1, false, true);
        nfa.addTransition(0, c, 1);
        return nfa;
    }
    
    // Helper method for WHITE_SPACE: (' '|\n|\r|\t)*
    private Automaton createWhitespaceNfa() {
        Automaton nfa = new AutomatonImpl();
        nfa.addState(0, true, true);  // Start and accept state
        
        // All whitespace characters
        char[] whitespace = {' ', '\n', '\r', '\t'};
        for (char c : whitespace) {
            nfa.addTransition(0, c, 0);  // Loop back for Kleene star
        }
        
        return nfa;
    }

}
