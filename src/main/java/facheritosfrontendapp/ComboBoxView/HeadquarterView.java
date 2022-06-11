package facheritosfrontendapp.ComboBoxView;

public class HeadquarterView {

    private Integer idHeadquarter;

    private String name;

    public HeadquarterView(Integer idHeadquarter, String name){
        this.idHeadquarter = idHeadquarter;
        this.name = name;
    }


    public Integer getIdHeadquarter() {
        return idHeadquarter;
    }

    public void setIdHeadquarter(Integer idHeadquarter) {
        this.idHeadquarter = idHeadquarter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString(){
        return this.getName();
    }
}
