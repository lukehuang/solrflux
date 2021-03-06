package net.moraleboost.flux.eval.stmt;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;

import net.moraleboost.flux.eval.EvalContext;
import net.moraleboost.flux.eval.EvalException;
import net.moraleboost.flux.eval.Expression;

public class DeleteStatement extends BaseStatement
{
    private String source;
    private Expression condition;
    private String nativeQuery;
    
    public DeleteStatement()
    {
        super();
    }
    
    public void setSource(String source)
    {
        this.source = source;
    }
    
    public String getSource()
    {
        return source;
    }
    
    public void setCondition(Expression condition)
    {
        this.condition = condition;
    }
    
    public Expression getCondition()
    {
        return condition;
    }
    
    public void setNativeQuery(String query)
    {
        this.nativeQuery = query;
    }
    
    public String getNativeQuery()
    {
        return nativeQuery;
    }

    public UpdateResponse execute(EvalContext ctx) throws EvalException
    {
        SolrServer server = getSolrServer(source, ctx);
        
        try {
            if (condition == null) {
                // delete all
                return server.deleteByQuery("*:*");
            } else {
                if (condition != null) {
                    // delete by query
                    String query = condition.toSolrQuery(ctx);
                    return server.deleteByQuery((String)query);
                } else if (nativeQuery != null) {
                    return server.deleteByQuery(nativeQuery);
                } else {
                    throw new EvalException("No query.");
                }
            }
        } catch (SolrServerException e) {
            throw new EvalException(e);
        } catch (IOException e) {
            throw new EvalException(e);
        }
    }
}
