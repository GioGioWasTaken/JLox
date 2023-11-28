package JLox;

public class Interpreter implements Expr.Visitor<Object> {
    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);
        switch (expr.operator.type) {
            case GREATER:
                return (double)left > (double)right;
            case GREATER_EQUAL:
                return (double)left >= (double)right;
            case LESS:
                return (double)left < (double)right;
            case LESS_EQUAL:
                return (double)left <= (double)right;
            case BANG_EQUAL: return !isEqual(left, right);
            case EQUAL_EQUAL: return isEqual(left, right);
            case MINUS:
                return (double)left - (double)right;
            case SLASH:
                return (double)left / (double)right;
            case STAR:
                return (double)left * (double)right;
            case PLUS:
                if(left instanceof Integer && right instanceof Integer){
                    return (double) left + (double) right;
                }
                if(left instanceof String && right instanceof String){
                    return (String) left + (String) right;
                }
                // otherwise we should return an error!
                break;
        }
        return null; // this code is unreachable.
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false; // avoid NullPointerException
        return a.equals(b);
    }


    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);
        switch (expr.operator.type) {
            case BANG:
                return !isTruthy(right);
            case MINUS:
                return -(double)right;
        }
        // Unreachable.
        return null;
    }

    private Boolean isTruthy(Object object) {
        if(object==null) return false; // if an object is null, return false
        if(object instanceof Boolean) return (boolean) object; // if it's a boolean return that value
        return true; // otherwise return true.
    }

    private Object evaluate(Expr expression) {
        return expression.accept(this);
    }
}
