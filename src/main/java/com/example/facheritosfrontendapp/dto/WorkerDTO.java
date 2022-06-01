package com.example.facheritosfrontendapp.dto;

import java.util.Date;

public class WorkerDTO extends PersonDTO {
    private Integer id_worker;

    private Integer id_headquarter;

    private String password;

    private Boolean state;

    protected Double salary;

    protected Date hired_date;

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
