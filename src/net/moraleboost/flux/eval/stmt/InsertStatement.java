package net.moraleboost.flux.eval.stmt;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import net.moraleboost.flux.eval.EvalContext;
import net.moraleboost.flux.eval.EvalException;
import net.moraleboost.flux.eval.Expression;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

public class InsertStatement extends BaseStatement
{
    private String destination;
    private List<String> fields;
    private List<Expression> values;
    private SelectStatement selectStatement;
    
    public InsertStatement()
    {
        super();
    }
    
    public void setDestination(String destination)
    {
        this.destination = destination;
    }
    
    public String getDestination()
    {
        return destination;
    }
    
    public void setFields(List<String> fields)
    {
        this.fields = fields;
    }
    
    public List<String> getFields()
    {
        return fields;
    }
    
    public void setValues(List<Expression> values)
    {
        this.values = values;
    }
    
    public List<Expression> getValues()
    {
        return values;
    }
    
    public void setSelectStatement(SelectStatement stmt)
    {
        this.selectStatement = stmt;
    }
    
    public SelectStatement getSelectStatement()
    {
        return selectStatement;
    }
    
    public UpdateResponse execute(EvalContext ctx) throws EvalException
    {
        if (fields == null) {
            throw new EvalException("No field specified.");
        }
        
        // SolrServerを取得
        SolrServer server = getSolrServer(destination, ctx);
        
        // InputDocumentを構築
        SolrInputDocument doc = new SolrInputDocument();
        
        if (values != null) {
            if (fields.size() != values.size()) {
                throw new EvalException("len(fields) != len(values)");
            }

            Iterator<String> fit = fields.iterator();
            Iterator<Expression> vit = values.iterator();
            while (fit.hasNext()) {
                doc.addField(fit.next(), vit.next().evaluate(ctx));
            }
        } else if (selectStatement != null) {
            SelectStatement.Result res = selectStatement.execute(ctx);
            SolrDocumentList results = res.getDocuments();
            for (SolrDocument resdoc: results) {
                for (String field: fields) {
                    doc.addField(field, resdoc.getFieldValue(field));
                }
            }
        } else {
            throw new EvalException("values and select statement are both null.");
        }
        
        // 追加
        try {
            return server.add(doc);
        } catch (SolrServerException e) {
            throw new EvalException(e);
        } catch (IOException e) {
            throw new EvalException(e);
        }
    }
}
