/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Render;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This is an encapsulated class that holds HTML contents
 *
 * @author Thanura
 */
public class HTML implements Serializable {

    private int indent = 0;
    String tagname = null;
    String innerHTML = null;
    String attributeText = null;
    HashMap<String, String> attribues = new HashMap<>();
    ArrayList<Serializable> innertags = new ArrayList<>();
    ArrayList<String> classes = new ArrayList<>();

    /**
     * This Constructor just crate the basic structure of HTML content.
     *
     * @param tagname Tag Type
     */
    public HTML(String tagname) {
        this.tagname = tagname;
    }

    /**
     * You can set element id from here.
     *
     * @param id
     */
    public void setID(String id) {
        addAttribute("id", id);
    }

    /**
     * You can add classes to element from here.
     *
     * @param s class name
     */
    public void addClass(String s) {
        classes.add(s);
        updateClassText();
    }

    /**
     * You can remove classes from here
     *
     * @param s class name
     */
    public void removeClass(String s) {
        classes.remove(s);
        updateClassText();
    }

    /**
     * To Finalize Class values.
     */
    public void updateClassText() {
        removeAttribute("class");
        if (!classes.isEmpty()) {
            String classtext = "";
            for (String classe : classes) {
                classtext += classe + " ";
            }
            addAttribute("class", classtext);
        }
    }

    /**
     * To update Attributes.
     */
    public void updateAttributeText() {
        attributeText = "";
        for (Map.Entry<String, String> entry : attribues.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            attributeText += " " + key + "=\"" + value + "\"";
        }
    }

    /**
     * To update InnerHTML Tags.
     */
    public void updateInnerHTML() {
        innerHTML = "";
        for (Serializable innertag : innertags) {
            if (innertag instanceof HTML) {
                ((HTML) innertag).setIndent(getIndentNum() + 1);
                ((HTML) innertag).updateInnerHTML();
            } else {
                innertag = getIndent(getIndentNum() + 1) + innertag;
            }
            innerHTML += innertag.toString() + "\n";
        }
    }

    /**
     * To add a new tag to body of this element.
     *
     * @param tag this can be any Serializable.
     */
    public void addTags(Serializable tag) {
        innertags.add(tag);
    }

    /**
     * You Can remove tags from id.
     *
     * @param index index number of element
     */
    public void removeTag(int index) {
        if (innertags.get(index) != null) {
            innertags.remove(index);
        }
    }

    /**
     * You Can remove tags from your tag.
     *
     * @param tag this can be any Serializable
     */
    public void removeTag(Serializable tag) {
        innertags.remove(tag);
    }

    /**
     * This will check your element body tag is empty or not.
     *
     * @return Boolean status.
     */
    public boolean isAEmptyTag() {
        return innertags.isEmpty();
    }

    /**
     * To add Attribute for element.
     *
     * @param attributename Attribute name
     * @param value Value
     */
    public void addAttribute(String attributename, String value) {
        attribues.put(attributename, value);
        updateAttributeText();
    }

    /**
     * To remove Attribute from element.
     *
     * @param attributename Attribute name.
     */
    public void removeAttribute(String attributename) {
        if (attribues.get(attributename) != null) {
            attribues.remove(attributename);
        }
        updateAttributeText();
    }

    /**
     * Set Indent spaces of element
     *
     * @param indent Integer value for tabs
     */
    public void setIndent(int indent) {
        this.indent = indent;
    }

    /**
     * Getter Method For indent.
     *
     * @return
     */
    public int getIndentNum() {
        return indent;
    }

    /**
     * Generate Indent spaces of each element
     *
     * @return String full with tabs
     */
    public String getIndent() {
        String s = "";
        for (int i = 0; i < indent; i++) {
            s += "\t";
        }
        return s;
    }

    /**
     * Generate Custom indent spaces
     *
     * @param ix Custom indent
     * @return String full with tabs
     */
    public String getIndent(int ix) {
        String s = "";
        for (int i = 0; i < ix; i++) {
            s += "\t";
        }
        return s;
    }

    /**
     * Generate all and Create final HTML
     *
     * @return HTML Contents
     */
    @Override
    public String toString() {
        updateAttributeText();
        if (isAEmptyTag()) {
            return getIndent() + "<" + tagname + attributeText + "/>";
        } else {
            return getIndent() + "<" + tagname + attributeText + ">\n" + innerHTML + getIndent() + "</" + tagname + ">";
        }
    }

}
