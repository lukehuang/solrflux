package net.moraleboost.flux.lang;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.antlr.runtime.tree.Tree;

import net.moraleboost.flux.eval.Expression;
import net.moraleboost.flux.eval.expr.AddExpression;
import net.moraleboost.flux.eval.expr.AndExpression;
import net.moraleboost.flux.eval.expr.BigDecimalLiteral;
import net.moraleboost.flux.eval.expr.BigIntegerLiteral;
import net.moraleboost.flux.eval.expr.BinaryOperatorExpression;
import net.moraleboost.flux.eval.expr.BooleanLiteral;
import net.moraleboost.flux.eval.expr.CallExpression;
import net.moraleboost.flux.eval.expr.CompareExpression;
import net.moraleboost.flux.eval.expr.DivideExpression;
import net.moraleboost.flux.eval.expr.DoubleLiteral;
import net.moraleboost.flux.eval.expr.EqualToExpression;
import net.moraleboost.flux.eval.expr.FloatLiteral;
import net.moraleboost.flux.eval.expr.IdLiteral;
import net.moraleboost.flux.eval.expr.IndexExpression;
import net.moraleboost.flux.eval.expr.IntegerLiteral;
import net.moraleboost.flux.eval.expr.ListExpression;
import net.moraleboost.flux.eval.expr.LongLiteral;
import net.moraleboost.flux.eval.expr.MultiplyExpression;
import net.moraleboost.flux.eval.expr.NegateExpression;
import net.moraleboost.flux.eval.expr.NotEqualToExpression;
import net.moraleboost.flux.eval.expr.NotExpression;
import net.moraleboost.flux.eval.expr.NullLiteral;
import net.moraleboost.flux.eval.expr.NumberLiteralExpression;
import net.moraleboost.flux.eval.expr.OrExpression;
import net.moraleboost.flux.eval.expr.StringLiteral;
import net.moraleboost.flux.eval.expr.SubtractExpression;
import net.moraleboost.flux.eval.expr.UnaryOperatorExpression;
import net.moraleboost.flux.eval.expr.WildcardLiteral;

public class ExpressionBuilder
{
    public ExpressionBuilder()
    {
    }
    
    public Expression build(Tree node)
    throws SyntaxException
    {
        if (node == null) {
            return null;
        }
        
        int type = node.getType();
        
        switch (type) {
        
        case SolrqlParser.T_OPERATOR_AND:
            // binary operator "AND"
            return buildBinaryOperator(node, new AndExpression());
        
        case SolrqlParser.T_OPERATOR_OR:
            // binary operator "OR"
            return buildBinaryOperator(node, new OrExpression());
            
        case SolrqlParser.T_OPERATOR_NOT:
            // binary operator "NOT"
            return buildBinaryOperator(node, new NotExpression());
            
        case SolrqlParser.T_OPERATOR_ADD:
            // binary operator "+"
            return buildBinaryOperator(node, new AddExpression());
            
        case SolrqlParser.T_OPERATOR_SUBTRACT:
            // binary operator "-"
            return buildBinaryOperator(node, new SubtractExpression());
            
        case SolrqlParser.T_OPERATOR_MULTIPLY:
            // binary operator "*"
            return buildBinaryOperator(node, new MultiplyExpression());
            
        case SolrqlParser.T_OPERATOR_DIVIDE:
            // binary operator "/"
            return buildBinaryOperator(node, new DivideExpression());
            
        case SolrqlParser.T_OPERATOR_PLUS:
            // unary operator "+"
            // ignore
            if (node.getChildCount() != 1) {
                throw new SyntaxException("Unary operator must take 1 operand.");
            }
            return build(node.getChild(0));
            
        case SolrqlParser.T_OPERATOR_MINUS:
            // unary operator "-"
            return buildUnaryOperator(node, new NegateExpression());
            
        case SolrqlParser.T_OPERATOR_CALL:
            // call operator "()"
            return buildCallOperator(node);
            
        case SolrqlParser.T_OPERATOR_EQUAL_TO_SINGLE:
            // "="
            return buildBinaryOperator(
                    node, new EqualToExpression(EqualToExpression.Type.Single));
            
        case SolrqlParser.T_OPERATOR_EQUAL_TO_DOUBLE:
            // "=="
            return buildBinaryOperator(
                    node, new EqualToExpression(EqualToExpression.Type.Double));
        
        case SolrqlParser.T_OPERATOR_NOT_EQUAL_TO_SINGLE:
            // "!="
            return buildBinaryOperator(
                    node, new NotEqualToExpression(NotEqualToExpression.Type.Single));
        
        case SolrqlParser.T_OPERATOR_NOT_EQUAL_TO_DOUBLE:
            // "!=="
            return buildBinaryOperator(
                    node, new NotEqualToExpression(NotEqualToExpression.Type.Double));
            
        case SolrqlParser.T_OPERATOR_LESS_THAN:
            // "<"
            return buildBinaryOperator(
                    node, new CompareExpression(CompareExpression.Type.LessThan));
            
        case SolrqlParser.T_OPERATOR_LESS_THAN_OR_EQUAL_TO:
            // "<="
            return buildBinaryOperator(
                    node, new CompareExpression(CompareExpression.Type.LessThanOrEqualTo));
            
        case SolrqlParser.T_OPERATOR_GREATER_THAN:
            // ">"
            return buildBinaryOperator(
                    node, new CompareExpression(CompareExpression.Type.GreaterThan));
            
        case SolrqlParser.T_OPERATOR_GREATER_THAN_OR_EQUAL_TO:
            // ">="
            return buildBinaryOperator(
                    node, new CompareExpression(CompareExpression.Type.GreaterThanOrEqualTo));
            
        case SolrqlParser.T_OPERATOR_INDEX:
            // "[i]"
            return buildIndexOperator(node);
            
        case SolrqlParser.ID:
            // ID literal
            return buildIdLiteral(node);
            
        case SolrqlParser.TRUE:
        case SolrqlParser.FALSE:
            // Boolean literal
            return buildBooleanLiteral(node);
            
        case SolrqlParser.STRING:
            // String literal
            return buildStringLiteral(node);
            
        case SolrqlParser.INTEGER:
            // Integer/Long/BigInteger literal
            return buildIntegerLiteral(node);
            
        case SolrqlParser.FLOAT:
            // Float/Double/BigDecimal literal
            return buildFloatLiteral(node);
            
        case SolrqlParser.NULL:
            // NULL
            return buildNullLiteral(node);
            
        case SolrqlParser.ASTERISK:
            // Wildcard literal
            return buildWildcardLiteral(node);
            
        case SolrqlParser.T_LIST:
            // List
            return buildList(node);

        default:
            throw new SyntaxException("Unknown token type: " + type);
            
        }
    }
    
    public CallExpression buildCallOperator(Tree node)
    throws SyntaxException
    {
        int childCount = node.getChildCount();
        
        if (childCount < 1) {
            throw new SyntaxException("Invalid function call.");
        }

        CallExpression ret = new CallExpression();
        
        ret.setFunction(build(node.getChild(0)));
        for (int i=1; i<childCount; ++i) {
            ret.addArgument(build(node.getChild(i)));
        }
        
        return ret;
    }
    
    public IndexExpression buildIndexOperator(Tree node)
    throws SyntaxException
    {
        if (node == null) {
            return null;
        }
        
        int childCount = node.getChildCount();
        
        if (childCount != 2) {
            throw new SyntaxException(
                    "Index operator must take 1 operand and 1 index.");
        }
        
        IndexExpression expr = new IndexExpression();
        
        Expression operand = build(node.getChild(0));
        Expression idx = build(node.getChild(1));
        
        expr.setOperand(operand);
        expr.setIndex(idx);
        
        return expr;
    }
    
    public BinaryOperatorExpression buildBinaryOperator(Tree node, BinaryOperatorExpression op)
    throws SyntaxException
    {
        if (node == null) {
            return null;
        }
        
        if (node.getChildCount() != 2) {
            throw new SyntaxException("Binary operator must take 2 operands.");
        }
        
        Expression lho = build(node.getChild(0));
        Expression rho = build(node.getChild(1));
        
        op.setLeftHandOperand(lho);
        op.setRightHandOperand(rho);
        
        return op;
    }
    
    public UnaryOperatorExpression buildUnaryOperator(Tree node, UnaryOperatorExpression op)
    throws SyntaxException
    {
        if (node == null) {
            return null;
        }
        
        if (node.getChildCount() != 1) {
            throw new SyntaxException("Unary operator must take 1 operand.");
        }
        
        Expression o = build(node.getChild(0));
        op.setOperand(o);
        
        return op;
    }
    
    public BooleanLiteral buildBooleanLiteral(Tree node)
    throws SyntaxException
    {
        if (node == null) {
            return null;
        }
        
        int type = node.getType();
        if (type == SolrqlParser.TRUE) {
            return new BooleanLiteral(true);
        } else if (type == SolrqlParser.FALSE){
            return new BooleanLiteral(false);
        } else {
            throw new SyntaxException("Unknown boolean literal.");
        }
    }
    
    public StringLiteral buildStringLiteral(Tree node)
    throws SyntaxException
    {
        if (node == null) {
            return null;
        }
        
        return new StringLiteral(Util.unescapeString(node.getText()));
    }
    
    public IdLiteral buildIdLiteral(Tree node)
    throws SyntaxException
    {
        if (node == null) {
            return null;
        }
        
        return new IdLiteral(Util.unescapeId(node.getText()));
    }
    
    public NumberLiteralExpression buildIntegerLiteral(Tree node)
    throws SyntaxException
    {
        if (node == null) {
            return null;
        }
        
        String text = node.getText();
        
        try {
            return new IntegerLiteral(Integer.parseInt(text));
        } catch (NumberFormatException e) {}
        
        try {
            return new LongLiteral(Long.parseLong(text));
        } catch (NumberFormatException e) {}
        
        try {
            return new BigIntegerLiteral(new BigInteger(text));
        } catch (NumberFormatException e) {}
        
        throw new SyntaxException("Invalid integer literal: " + text);
    }
    
    public NumberLiteralExpression buildFloatLiteral(Tree node)
    throws SyntaxException
    {
        if (node == null) {
            return null;
        }
        
        String text = node.getText();
        
        try {
            float val = Float.parseFloat(text);
            if (!Float.isInfinite(val) && !Float.isNaN(val)) {
                return new FloatLiteral(val);
            }
        } catch (NumberFormatException e) {}
        
        try {
            double val = Double.parseDouble(text);
            if (!Double.isInfinite(val) && !Double.isNaN(val)) {
                return new DoubleLiteral(val);
            }
        } catch (NumberFormatException e) {}
        
        try {
            return new BigDecimalLiteral(new BigDecimal(text));
        } catch (NumberFormatException e) {}
        
        throw new SyntaxException("Invalid float literal: " + text);
    }
    
    public NullLiteral buildNullLiteral(Tree node)
    {
        if (node == null) {
            return null;
        }
        
        return new NullLiteral();
    }
    
    public WildcardLiteral buildWildcardLiteral(Tree node)
    {
        if (node == null) {
            return null;
        }
        
        return new WildcardLiteral();
    }
    
    public ListExpression buildList(Tree node)
    throws SyntaxException
    {
        if (node == null) {
            return null;
        }
        
        ListExpression expr = new ListExpression();
        
        int childCount = node.getChildCount();
        for (int i=0; i<childCount; ++i) {
            expr.addElement(build(node.getChild(i)));
        }
        
        return expr;
    }
}
