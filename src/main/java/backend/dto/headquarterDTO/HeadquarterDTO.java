package backend.dto.headquarterDTO;

public  class HeadquarterDTO {
    protected Integer id_headquarter;
    protected Integer id_city;
    protected String name;
    protected String cellphone;
    protected String email;
    protected String address;

    public Integer getId_headquarter() {
        return id_headquarter;
    }

    public void setId_headquarter(Integer id_headquarter) {
        this.id_headquarter = id_headquarter;
    }

    public Integer getId_city() {
        return id_city;
    }

    public void setId_city(Integer id_city) {
        this.id_city = id_city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}