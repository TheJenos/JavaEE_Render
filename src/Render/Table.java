/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Render;

/**
 * This is an encapsulated class that holds HTML contents of table
 *
 * @author Thanura
 */
public class Table extends HTML {

    public Table() {
        super("table");
    }

    /**
     * set Border to table
     *
     * @param x width of border
     */
    public void setBorders(int x) {
        addAttribute("border", x + "");
    }

    /**
     * this will crate a new row and return it.
     *
     * @return HTML row
     */
    public HTML AddRow() {
        HTML tr = new HTML("tr");
        addTags(tr);
        return tr;
    }

    /**
     * Generate HTML contents from table
     *
     * @return HTML content
     */
    @Override
    public String toString() {
        updateInnerHTML();
        return super.toString(); //To change body of generated methods, choose Tools | Templates.
    }

}
