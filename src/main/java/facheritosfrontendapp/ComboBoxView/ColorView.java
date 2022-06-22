package facheritosfrontendapp.ComboBoxView;

public class ColorView {

    private String color;

    private Integer idColor;

    public ColorView(Integer idColor, String color) {
        this.color = color;
        this.idColor = idColor;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getIdColor() {
        return idColor;
    }

    public void setIdColor(Integer idColor) {
        this.idColor = idColor;
    }

    /**
     * toString: void -> String
     * purpose: This method maps the object to a string attribute for the combobox view
     */
    @Override
    public String toString(){
        return this.getColor();
    }
}
