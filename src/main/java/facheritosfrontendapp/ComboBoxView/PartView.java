package facheritosfrontendapp.ComboBoxView;

public class PartView {

    private Integer idPart;
    private String name;

    public PartView(Integer idPart, String name) {
        this.idPart = idPart;
        this.name = name;
    }

    public Integer getIdPart() {
        return idPart;
    }

    public void setIdPart(Integer idPart) {
        this.idPart = idPart;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * toString: void -> String
     * purpose: This method maps the object to a string attribute for the combobox view
     */
    @Override
    public String toString(){
        return this.getName();
    }
}
