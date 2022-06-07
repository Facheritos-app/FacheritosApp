package backend.dto.personDTO;

import java.util.Date;

public class WorkerDTO extends PersonDTO {
    private Integer id_worker;

    private Integer id_headquarter;

    private String password;

    private Integer id_rol;

    private String rol;

    private Boolean state;

    private Double salary;

    private Date hired_date;



    public Integer getId_worker() {
        return id_worker;
    }

    public void setId_worker(Integer id_worker) {
        this.id_worker = id_worker;
    }

    public Integer getId_headquarter() {
        return id_headquarter;
    }

    public void setId_headquarter(Integer id_headquarter) {
        this.id_headquarter = id_headquarter;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getId_rol() {
        return id_rol;
    }

    public void setId_rol(Integer id_rol) {
        this.id_rol = id_rol;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public Date getHired_date() {
        return hired_date;
    }

    public void setHired_date(Date hired_date) {
        this.hired_date = hired_date;
    }
}
