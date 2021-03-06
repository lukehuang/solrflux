package net.moraleboost.flux.eval.expr.func;

import java.math.BigInteger;
import java.util.List;

import net.moraleboost.flux.eval.EvalContext;
import net.moraleboost.flux.eval.EvalException;

public class BigIntegerFunction extends BaseFunction
{
    public BigIntegerFunction()
    {
        super();
    }

    @Override
    public BigInteger call(List<Object> arguments, EvalContext ctx)
            throws EvalException
    {
        if (arguments.size() == 1) {
            Object arg = arguments.get(0);
            if (arg == null) {
                return null;
            } else if (isNumber(arg)) {
                return convertToBigInteger((Number)arg);
            } else if (isString(arg)) {
                try {
                    return new BigInteger((String)arg);
                } catch (Exception e) {
                    throw new EvalException(e);
                }
            } else {
                throw new EvalException(
                        "Can't convert " + arg.getClass().getCanonicalName() +
                        " to a BigInteger.");
            }
        } else {
            throw new EvalException("BIGINTEGER function must take 1 argument.");
        }
    }
}
