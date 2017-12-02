/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Render;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * This A Factory Class That Generate HTML Contents For Print.<br>
 * <p>
 * <u>Basic rules of platform</u><br>
 * <b>[[--- Getter name ---]]</b><br>
 * Ex
 * <pre>
 * {@code
 * [[Uname]]
 * <a href="?delete=[[Uid]]">[[Uname]]</a>
 * }
 * </pre>
 * <b>[[--- Getter name ---.--- Another getter name ---]]</b><br>
 * When you need to execute a another methods that in return Object.
 * <pre>
 * {@code
 * [[UserType.Name]]
 * [[Address.Address1]]
 * }
 * </pre>
 * <b>[[#--- Non getter name ---,-- Parameters.. --]]</b><br>
 * I use a single letter for identify datatype of parameter <b> In this case we
 * can only use primitive data types (Expect Float and Double) and String as
 * parameters.</b><br>
 * <p>
 * <ul>
 * <li><pre> Integer  I  Ex I10 </pre></li>
 * <li><pre> Long     L  Ex L2000 </pre></li>
 * <li><pre> Byte     B  Ex B12 </pre></li>
 * <li><pre> String   S  Ex Sthanura nadun </pre></li>
 * <li><pre> Boolean     Ex ture </pre></li>
 * </ul>
 * Ex<br>
 * <pre>
 * {@code
 * [[Uname.#toString]]
 * <a href="?delete=[[Uid]]">[[Uname.#charAt,I0]]</a>
 * <a href="?delete=[[Uid]]">[[Uname.#concat,Slol]]</a>
 * <a href="?delete=[[Uid]]">[[Uname.#some,true,Slol]]</a>
 * }
 * </pre>
 * <b>{@code TIME{<--- Date time pattern ---> -> [[<--- Date Object --->]]}}</b><br>
 * We use TIME{} tag to format date like SimpleDateFormat.
 * <pre>
 * {@code
 *  TIME{yyyy MM->[[Date]]}
 *  TIME{yyyy MM hh:mm:ss->[[Date]]}
 * }
 * </pre> In Java there are ScriptEngine For use. So in this platform,I used a
 * JavaScript Engine for Calculations and Handle Logics.
 * <p>
 * <u>Basic Calculation</u>
 * <pre>
 * {@code
 *  JS{3+2}
 * }
 * Output : 5
 * </pre>
 * <u>Calculation With List Data</u>
 * <pre>
 * if data-1 was "name".
 * {@code
 *  JS{'[[data-0]]'+2}
 * }
 * Output : name2
 * </pre>
 * <pre>
 * if Sid was 2.
 * {@code
 *  JS{[[Sid]]+2}
 * }
 * Output : 4
 * </pre>
 *
 * @author Thanura
 */
public class HTMLFactory {

    /**
     * In this method, If you are using a criteria with projection, you need
     * this. because that Data list return Object Array List. In that case use
     * this pattern for locate data.
     * <p>
     * 1st Projection - [[data-0]] <br>
     * 2nd Projection - [[data-1]]
     *
     * @param cr Data list
     * @param data in this map, key value is the column text (You can also apply
     * HTML contents) and value is the pattern
     * @return return a HTML Object with that contain with HTML and that List
     * data
     */
    public Table SQLtableWithProjection(List cr, Map data) throws Exception {
        Table table = new Table();
        table.setIndent(2);
        HTML tableheaders = table.AddRow();
        for (Iterator iterator = data.keySet().iterator(); iterator.hasNext();) {
            Object next = iterator.next();
            HTML th = new HTML("th");
            th.addTags(next.toString());
            tableheaders.addTags(th);
        }
        List<Object[]> datalines = (List<Object[]>) cr;
        for (Object[] object : datalines) {
            HTML tablerows = table.AddRow();
            for (Iterator iterator = data.values().iterator(); iterator.hasNext();) {
                Object next = iterator.next();
                HTML td = new HTML("td");
                String s = next.toString();
                for (int i = 0; i < object.length; i++) {
                    Object object1 = object[i];
                    s = s.replaceAll("\\[\\[data-" + i + "\\]\\]", object1.toString());
                    //engine.put(object1.toString().trim(), object1);
                }
                Matcher mss = Pattern.compile("TIME\\{([^}]+)\\}").matcher(s);
                while (mss.find()) {
                    String dateString[] = mss.group(1).split("->");
                    String dats = dateString[1];
                    Date d = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.0").parse(dats);
                    String newdate = new SimpleDateFormat(dateString[0]).format(d);
                    s = s.replaceAll("TIME\\{" + regexFilter(mss.group(1)) + "\\}", newdate);
                }
                s = javascriptEngin(s);
                td.addTags(s);
                tablerows.addTags(td);
            }
        }
        return table;
    }

    /**
     * In this method, If you are using a criteria without projection, you need
     * this. because that Data list return POJO Object List. In that case use
     * this pattern for locate data.
     * <p>
     * getUname - [[Uname]] <br>
     * getUpass - [[Upass]]
     *
     * @param cr Data list
     * @param data in this map, key value is the column text (You can also apply
     * HTML contents) and value is the pattern
     * @return return a HTML Object with that contain with HTML and that List
     * data
     */
    public Table SQLtable(List cr, Map data) throws Exception {
        Table table = new Table();
        table.setIndent(2);
        HTML tableheaders = table.AddRow();
        for (Iterator iterator = data.keySet().iterator(); iterator.hasNext();) {
            Object next = iterator.next();
            HTML th = new HTML("th");
            th.addTags(next.toString());
            tableheaders.addTags(th);
        }
        for (Object object : cr) {
            HTML tablerows = table.AddRow();
            Class c = object.getClass();
            for (Iterator iterator = data.values().iterator(); iterator.hasNext();) {
                Object next = iterator.next();
                HTML td = new HTML("td");
                String s = next.toString();
                Matcher m = Pattern.compile("\\[\\[([^]]+)\\]\\]").matcher(s);
                while (m.find()) {
                    String[] sub = m.group(1).split("\\.");
                    Object retrunO = null;
                    Object pojo = object;
                    c = object.getClass();
                    for (int i = 0; i < sub.length; i++) {
                        String string = sub[i];
                        if (string.startsWith("#")) {
                            String paras[] = string.substring(1).split(",");
                            if (paras.length > 1) {
                                Class[] classes = new Class[paras.length - 1];
                                Object[] objs = new Object[paras.length - 1];
                                for (int j = 1; j < paras.length; j++) {
                                    String para = paras[j];
                                    DataPill dp = getRealObject(para);
                                    classes[j - 1] = dp.getDatatype();
                                    objs[j - 1] = dp.getObject();
                                }
                                retrunO = c.getMethod(paras[0], classes).invoke(pojo, objs);
                            } else {
                                retrunO = c.getMethod(string.substring(1), null).invoke(pojo, null);
                            }
                        } else {
                            retrunO = c.getMethod("get" + string, null).invoke(pojo, null);
                        }
                        c = retrunO.getClass();
                        pojo = retrunO;
                    }
                    //engine.put(retrunO.toString().trim(), retrunO);
                    s = s.replaceAll("\\[\\[" + m.group(1) + "\\]\\]", retrunO.toString());
                    Matcher mss = Pattern.compile("TIME\\{([^}]+)\\}").matcher(s);
                    while (mss.find()) {
                        String dateString[] = mss.group(1).split("->");
                        String dats = dateString[1];
                        Date d = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.0").parse(dats);
                        String newdate = new SimpleDateFormat(dateString[0]).format(d);
                        s = s.replaceAll("TIME\\{" + regexFilter(mss.group(1)) + "\\}", newdate);
                    }
                }
                s = javascriptEngin(s);
                td.addTags(s);
                tablerows.addTags(td);
            }
        }
        return table;
    }

    /**
     * If you are using a Object to use this pattern, you need this method.
     * <p>
     * getUname - [[Uname]] <br>
     * toString - [[#toString]]
     *
     * @param obj Data Object
     * @param format This String is the pattern
     * @return return a HTML Object with that contain with HTML and that List
     * data
     * @throws java.lang.Exception
     */
    public String objectFormat(Object obj, String format) throws Exception {
        Class c = obj.getClass();
        Matcher m = Pattern.compile("\\[\\[([^]]+)\\]\\]").matcher(format);
        while (m.find()) {
            String[] sub = m.group(1).split("\\.");
            Object retrunO = null;
            Object pojo = obj;
            c = obj.getClass();
            for (int i = 0; i < sub.length; i++) {
                String string = sub[i];
                if (string.startsWith("#")) {
                    String paras[] = string.substring(1).split(",");
                    if (paras.length > 1) {
                        Class[] classes = new Class[paras.length - 1];
                        Object[] objs = new Object[paras.length - 1];
                        for (int j = 1; j < paras.length; j++) {
                            String para = paras[j];
                            DataPill dp = getRealObject(para);
                            classes[j - 1] = dp.getDatatype();
                            objs[j - 1] = dp.getObject();
                        }
                        retrunO = c.getMethod(paras[0], classes).invoke(pojo, objs);
                    } else {
                        retrunO = c.getMethod(string.substring(1), null).invoke(pojo, null);
                    }
                } else {
                    retrunO = c.getMethod("get" + string, null).invoke(pojo, null);
                }
                c = retrunO.getClass();
                pojo = retrunO;
            }
            //engine.put(retrunO.toString().trim(), retrunO);
            format = format.replaceAll("\\[\\[" + m.group(1) + "\\]\\]", retrunO.toString());
            Matcher mss = Pattern.compile("TIME\\{([^}]+)\\}").matcher(format);
            while (mss.find()) {
                String dateString[] = mss.group(1).split("->");
                String dats = dateString[1];
                Date d = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.0").parse(dats);
                String newdate = new SimpleDateFormat(dateString[0]).format(d);
                format = format.replaceAll("TIME\\{" + regexFilter(mss.group(1)) + "\\}", newdate);
            }
            format = javascriptEngin(format);
        }
        return format;
    }

    /**
     * In this method, If are using a criteria with projection, you need this.
     * because that Data list return Object Array List. In that case use this
     * pattern for locate data.
     * <p>
     * 1st Projection - [[data-0]] <br>
     * 2nd Projection - [[data-1]]
     *
     * @param cr Data list
     * @param format This String is the pattern
     * @return a String with data binds
     * @throws java.lang.Exception
     */
    public String LooperTextWithProjection(List cr, String format) throws Exception {
        String finaltxt = "";
        List<Object[]> datalines = (List<Object[]>) cr;
        for (Object[] object : datalines) {
            String line = format;
            for (int i = 0; i < object.length; i++) {
                Object object1 = object[i];
                line = line.replaceAll("\\[\\[data-" + i + "\\]\\]", object1.toString());
                //engine.put(object1.toString().trim(), object1);
            }
            Matcher mss = Pattern.compile("TIME\\{([^}]+)\\}").matcher(line);
            while (mss.find()) {
                String dateString[] = mss.group(1).split("->");
                String dats = dateString[1];
                Date d = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.0").parse(dats);
                String newdate = new SimpleDateFormat(dateString[0]).format(d);
                line = line.replaceAll("TIME\\{" + regexFilter(mss.group(1)) + "\\}", newdate);
            }
            line = javascriptEngin(line);
            finaltxt += line + "\n";
        }
        return finaltxt;
    }

    /**
     * In this method, If are using a criteria with projection, you need this.
     * because that Data list return Object. In that case use this pattern for
     * locate data.
     * <p>
     * getUname - [[Uname]] <br>
     * toString - [[#toString]]
     *
     * @param cr Data list
     * @param format This String is the pattern
     * @return a String with data binds
     */
    public String LooperText(List cr, String format) throws Exception {
        String finaltxt = "";
        for (Object object : cr) {
            Class c = object.getClass();
            String s = format;
            Matcher m = Pattern.compile("\\[\\[([^]]+)\\]\\]").matcher(s);
            while (m.find()) {
                String[] sub = m.group(1).split("\\.");
                Object retrunO = null;
                Object pojo = object;
                c = object.getClass();
                for (int i = 0; i < sub.length; i++) {
                    String string = sub[i];
                    if (string.startsWith("#")) {
                        String paras[] = string.substring(1).split(",");
                        if (paras.length > 1) {
                            Class[] classes = new Class[paras.length - 1];
                            Object[] objs = new Object[paras.length - 1];
                            for (int j = 1; j < paras.length; j++) {
                                String para = paras[j];
                                DataPill dp = getRealObject(para);
                                classes[j - 1] = dp.getDatatype();
                                objs[j - 1] = dp.getObject();
                            }
                            retrunO = c.getMethod(paras[0], classes).invoke(pojo, objs);
                        } else {
                            retrunO = c.getMethod(string.substring(1), null).invoke(pojo, null);
                        }
                    } else {
                        retrunO = c.getMethod("get" + string, null).invoke(pojo, null);
                    }
                    c = retrunO.getClass();
                    pojo = retrunO;
                }
                //engine.put(retrunO.toString().trim(), retrunO);
                s = s.replaceAll("\\[\\[" + m.group(1) + "\\]\\]", retrunO.toString());
            }
            Matcher mss = Pattern.compile("TIME\\{([^}]+)\\}").matcher(s);
            while (mss.find()) {
                String dateString[] = mss.group(1).split("->");
                String dats = dateString[1];
                Date d = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.0").parse(dats);
                String newdate = new SimpleDateFormat(dateString[0]).format(d);
                s = s.replaceAll("TIME\\{" + regexFilter(mss.group(1)) + "\\}", newdate);
            }
            s = javascriptEngin(s);
            finaltxt += s + "\n";
        }
        return finaltxt;
    }

    /**
     * This method is not very useful. I use this method for separate string and
     * cast into datatype
     *
     * @param s Parameter
     * @return This will return a Datapill Object
     */
    private DataPill getRealObject(String s) throws Exception {
        String data[] = s.split(":");
        String ss = data[0].trim();
        if (data.length > 1) {
            if (ss.equalsIgnoreCase("String")) {
                return new DataPill(String.class, data[1]);
            } else if (ss.equalsIgnoreCase("int")) {
                return new DataPill(int.class, Integer.parseInt(data[1]));
            } else if (ss.equalsIgnoreCase("long")) {
                return new DataPill(long.class, Long.parseLong(data[1]));
            } else if (ss.equalsIgnoreCase("char")) {
                return new DataPill(char.class, data[1].charAt(0));
            } else if (ss.equalsIgnoreCase("byte")) {
                return new DataPill(byte.class, Byte.parseByte(data[1]));
            } else if (ss.equalsIgnoreCase("float")) {
                return new DataPill(float.class, Float.parseFloat(data[1]));
            } else if (ss.equalsIgnoreCase("double")) {
                return new DataPill(double.class, Double.parseDouble(data[1]));
            } else if (ss.equalsIgnoreCase("boolean")) {
                return new DataPill(boolean.class, Boolean.parseBoolean(data[1]));
            } else {
                return new DataPill(s.getClass(), data[1]);
            }
        } else {
            return new DataPill(String.class, data[1]);
        }
    }

    private ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");

    /**
     * When we insert a String there can be issue with Regex.So i create a
     * filter.
     *
     * @param s String that need to be filter.
     * @return This will return a Regex Filtered String.
     */
    public String regexFilter(String s) {
        return Pattern.quote(s);
    }

    public String javascriptEngin(String s) throws Exception {
//        Matcher ms = Pattern.compile("JS\\{([^}]+)\\}").matcher(s);
//        while (ms.find()) {
//            s = s.replaceAll("JS\\{" + regexFilter(ms.group(1)) + "\\}", eval(ms.group(1)).toString());
//        }
//        return s;
        String codes[] = s.split("JS");
        for (int i = 0; i < codes.length; i++) {
            String code = codes[i];
            Matcher ms = Pattern.compile("\\{([^\\u0000]+)\\}").matcher(code);
            while (ms.find()) {
                s = s.replaceAll("JS\\{" + regexFilter(ms.group(1)) + "\\}", eval(ms.group(1)).toString());
            }
        }
        return s;
    }

    /**
     * I use this method of execute JavaScript Logics.
     *
     * @param input JavaScript as String.
     * @return This returns object that create on that Script.
     */
    public Object eval(String input) throws Exception {
        try {
            return engine.eval(input);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
