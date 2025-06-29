package it.uniroma3.siw.dto;

import java.time.LocalDate;

public class AuthorSearchDTO {

    private String name;
    private String surname;
    private String nationality;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = (name != null && !name.trim().isEmpty()) ? name.trim() : null;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = (surname != null && !surname.trim().isEmpty()) ? surname.trim() : null;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = (nationality != null && !nationality.trim().isEmpty()) ? nationality.trim() : null;
    }
}
