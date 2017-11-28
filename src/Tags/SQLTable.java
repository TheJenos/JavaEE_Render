/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tags;

import Render.Alert;
import Render.HTMLFactory;
import Render.Table;
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * This is a Custom Tag Class for handle SQLTable <br>
 * Ex<br>
 * {@code 
 * <prefix:SQLTable criteria="List" projection="true/false">
 *  column name1 -- pattern1
 *  column name2 -- pattern2
 * </prefix:SQLTable>
 * }
 * @author Thanura
 */
public class SQLTable extends SimpleTagSupport {

    private List criteria;
    private String cls = "";
    private boolean projection = false;
    StringWriter sw = new StringWriter();

    @Override
    public void doTag() throws JspException, IOException {
        JspWriter out = getJspContext().getOut();
        try {
            getJspBody().invoke(sw);
            LinkedHashMap l = new LinkedHashMap();
            String lines[] = sw.getBuffer().toString().trim().split("\n");
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                l.put(line.split("--")[0].trim(), line.split("--")[1].trim());
            }
            Table t = (projection) ? new HTMLFactory().SQLtableWithProjection(criteria,l): new HTMLFactory().SQLtable(criteria, l);
            t.addClass(cls);
            out.println(t);
        } catch (Exception ex) {
            out.println(new Alert(ex.toString()).danger());
        }
    }

    public void setCls(String Class) {
        this.cls = Class;
    }

    public void setProjection(boolean projection) {
        this.projection = projection;
    }

    public void setCriteria(List criteria) {
        this.criteria = criteria;
    }

}
