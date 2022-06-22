package facheritosfrontendapp.ComboBoxView;

public class ModelView {

    private Integer idModel;

    private String name;

    public ModelView(Integer idModel, String name){
        this.idModel = idModel;
        this.name = name;
    }

    public Integer getIdModel() {
        return idModel;
    }

    public void setIdModel(Integer idModel) {
        this.idModel = idModel;
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
