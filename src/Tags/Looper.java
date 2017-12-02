/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Tags;

import Render.Alert;
import Render.HTMLFactory;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * This is a Custom Tag Class for handle LooperText <br>
 * Ex<br>
 * {@code 
 * <prefix:Looper criteria="List" projection="true/false">
 *  Pattern
 * </prefix:Looper>
 * }
 * @author Thanura
 */
public class Looper extends SimpleTagSupport {

    private List criteria;
    private boolean projection = false;
    StringWriter sw = new StringWriter();

    public void doTag() throws JspException, IOException {
        getJspBody().invoke(sw);
        JspWriter out = getJspContext().getOut();
        try {
            String t = (projection) ? new HTMLFactory().LooperTextWithProjection(criteria, sw.getBuffer().toString()) : new HTMLFactory().LooperText(criteria, sw.getBuffer().toString());
            out.println(t);
        } catch (Exception e) {
            e.printStackTrace();
            out.println(new Alert(e.toString()).danger());
        }

    }

    public void setProjection(boolean projection) {
        this.projection = projection;
    }

    public void setCriteria(List criteria) {
        this.criteria = criteria;
    }

}
