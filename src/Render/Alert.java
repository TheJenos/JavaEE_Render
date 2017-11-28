/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Render;

/**
 * This is an encapsulated class that holds HTML DIV content with alert class.
 *
 * @author Thanura
 */
public class Alert extends HTML {

    public Alert(String s) {
        super("div");
        this.addClass("alert");
        this.addTags(s);
    }

    /**
     * This will add "alert-danger" to classes.
     *
     * @return this Object
     */
    public Alert danger() {
        this.addClass("alert-danger");
        return this;
    }

    /**
     * This will add "alert-success" to classes.
     *
     * @return this Object
     */
    public Alert success() {
        this.addClass("alert-success");
        return this;
    }

    /**
     * This will add "alert-secondary" to classes.
     *
     * @return this Object
     */
    public Alert secondary() {
        this.addClass("alert-secondary");
        return this;
    }

    /**
     * This will add "alert-warning" to classes.
     *
     * @return this Object
     */
    public Alert warning() {
        this.addClass("alert-warning");
        return this;
    }

    /**
     * This will add "alert-info" to classes.
     *
     * @return this Object
     */
    public Alert info() {
        this.addClass("alert-info");
        return this;
    }

    /**
     * This will add "alert-light" to classes.
     *
     * @return this Object
     */
    public Alert light() {
        this.addClass("alert-light");
        return this;
    }

    /**
     * This will add "alert-dark" to classes.
     *
     * @return this Object
     */
    public Alert dark() {
        this.addClass("alert-dark");
        return this;
    }

    /**
     * Generate HTML Content of this Alert.
     *
     * @return this Object
     */
    @Override
    public String toString() {
        updateInnerHTML();
        return super.toString(); //To change body of generated methods, choose Tools | Templates.
    }

}
