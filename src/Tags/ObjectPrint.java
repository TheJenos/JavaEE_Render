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
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * This is a Custom Tag Class for handle LooperText <br>
 * Ex<br>
 * {@code 
 * <prefix:ObjectPrint obj="Object">
 *  Pattern
 * </prefix:ObjectPrint>
 * }
 * @author Thanura
 */
public class ObjectPrint extends SimpleTagSupport {

    private Object obj;
    StringWriter sw = new StringWriter();

    @Override
    public void doTag() throws JspException ,IOException{
        getJspBody().invoke(sw);
        JspWriter out = getJspContext().getOut();
        try {
            String t = new HTMLFactory().objectFormat(obj, sw.getBuffer().toString());
            out.println(t);
        } catch (Exception e) {
            e.printStackTrace();
            out.println(new Alert(e.toString()).danger());
        }
    }

    public void setObj(Object Obj) {
        this.obj = Obj;
    }

}
