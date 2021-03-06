package net.moraleboost.flux.eval.expr.func;

import java.util.List;

import net.moraleboost.flux.eval.EvalContext;
import net.moraleboost.flux.eval.EvalException;
import net.moraleboost.flux.eval.Expression;

public interface Function extends Expression
{
    public Object call(List<Object> arguments, EvalContext ctx) throws EvalException;
}
