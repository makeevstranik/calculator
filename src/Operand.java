enum Operand {
    PLUS, MINUS, DIV, MULTI;
    int makeCalculation(int[] operands) {
        int left = operands[0];
        int right = operands[1];
        return switch (this) {
            case DIV -> left / right;
            case MULTI -> left * right;
            case MINUS -> left - right;
            case PLUS -> left + right;
        };
    }
}