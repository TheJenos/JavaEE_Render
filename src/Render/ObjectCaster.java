/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Render;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Object Caster is very useful for Convert POJO file to JSON. in that case we
 * need to mark rage to collect data.
 *
 * @author Thanura
 */
public class ObjectCaster {

    public interface Action extends Serializable {
        public Object Execute(Object ob);
    }

    List obj;
    LinkedHashMap<String, Serializable> map = new LinkedHashMap<String, Serializable>();

    public ObjectCaster(List sr) {
        obj = sr;
    }

    /**
     * This method helps you set range of JSONArray.
     *
     * @param key key value to identify.
     * @param val pattern text to get Data.
     */
    public void addToMap(String key, String val) {
        map.put(key, val);
    }

    /**
     * This method helps you set range of JSONArray.
     *
     * @param val pattern text to get Data.
     */
    public void addToMap(String val) {
        map.put(val, val);
    }

    /**
     * This method helps you set range of JSONArray.
     *
     * @param path key value to identify.
     * @param val Action that happened to be.
     */
    public void addAction(String path, Action val) {
        map.put(path, val);
    }

    /**
     * This method helps you set range of JSONArray.
     *
     * @param path key value to identify.
     * @param val Action that happened to be.
     */
    public void addData(String path,final Object val) {
        map.put(path, new Action() {
            @Override
            public Object Execute(Object ob) {
                return val;
            }
        });
    }
    
    /**
     * Create Sub paths.
     *
     * @return
     */
    private void subPath(JSONObject jo, String ss, Object obj) {
        String[] sub = ss.split("\\.");
        JSONObject currentjo = jo;
        if (sub.length > 1) {
            for (int i = 0; i < sub.length - 1; i++) {
                String string = sub[i];
                if (currentjo.isNull(string)) {
                    JSONObject json = new JSONObject();
                    currentjo.put(string, json);
                    currentjo = json;
                } else {
                    currentjo = currentjo.getJSONObject(string);
                }
            }
        }
        currentjo.put(sub[sub.length - 1], obj);
    }

    /**
     * Get Sub paths.
     *
     * @return
     */
    private JSONObject getPath(JSONObject jo, String ss) {
        String[] sub = ss.split("\\.");
        JSONObject currentjo = jo;
        if (sub.length > 1) {
            for (int i = 0; i < sub.length; i++) {
                String string = sub[i];
                if (currentjo.isNull(string)) {
                    JSONObject json = new JSONObject();
                    currentjo.put(string, json);
                    currentjo = json;
                } else {
                    currentjo = currentjo.getJSONObject(string);
                }
            }
        }
        return currentjo;
    }
    
    /**
     * This method return the map of range.
     *
     * @return
     */
    public LinkedHashMap<String, Serializable> getMap() {
        return map;
    }

    /**
     * This returns the casted JSONArray.
     *
     * @return
     */
    public JSONArray Cast() throws Exception {
        JSONArray ret = new JSONArray();
        for (Object object : obj) {
            JSONObject jsono = new JSONObject();
            for (Map.Entry<String, Serializable> entry : map.entrySet()) {
                String key = entry.getKey();
                if (entry.getValue() instanceof Action) {
                    Action a = (Action) entry.getValue();
                    subPath(jsono, key, a.Execute(object));
                } else {
                    String value = entry.getValue().toString();
                    String[] sub = value.split("\\.");
                    Object retrunO = null;
                    Object pojo = object;
                    Class c = object.getClass();
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
                        if (retrunO != null) {
                            c = retrunO.getClass();
                        }
                        pojo = retrunO;
                    }
                    subPath(jsono, key, pojo);
                }
            }
            ret.put(jsono);
        }
        return ret;
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
                return new DataPill(String.class,
                        data[1]);

            } else if (ss.equalsIgnoreCase("int")) {
                return new DataPill(int.class,
                        Integer.parseInt(data[1]));

            } else if (ss.equalsIgnoreCase("long")) {
                return new DataPill(long.class,
                        Long.parseLong(data[1]));

            } else if (ss.equalsIgnoreCase("char")) {
                return new DataPill(char.class,
                        data[1].charAt(0));

            } else if (ss.equalsIgnoreCase("byte")) {
                return new DataPill(byte.class,
                        Byte.parseByte(data[1]));

            } else if (ss.equalsIgnoreCase("float")) {
                return new DataPill(float.class,
                        Float.parseFloat(data[1]));

            } else if (ss.equalsIgnoreCase("double")) {
                return new DataPill(double.class,
                        Double.parseDouble(data[1]));

            } else if (ss.equalsIgnoreCase("boolean")) {
                return new DataPill(boolean.class,
                        Boolean.parseBoolean(data[1]));
            } else {
                return new DataPill(s.getClass(), data[1]);

            }
        } else {
            return new DataPill(String.class,
                    data[1]);
        }
    }

}
