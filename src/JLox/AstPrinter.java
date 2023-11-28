package JLox;


class ASTPrinter implements Expr.Visitor<String> {
    String print(Expr expr) {
        return expr.accept(this);
    }
    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }
    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize("group", expr.expression);
    }
    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value == null) return "nil";
        return expr.value.toString();
    }
    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }
    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this)); // recursively call expr.accept to print out the entire tree
            /* A walkthrough example: Parenthesize( *, literalExpr, literalExpr)
            1. (
            2. ( *
            3. the current expr is a literal, so expr.accept calls visitLiteral, which here would just add the string value of the literal
            4. ( * 5
            5. ( * 5 6)
             */
        }
        builder.append(")");
        return builder.toString();
    }

}