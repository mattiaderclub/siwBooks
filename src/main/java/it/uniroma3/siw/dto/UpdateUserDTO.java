package it.uniroma3.siw.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateUserDTO {

	@NotBlank(message = "Il nome è obbligatorio")
    private String name;

    @NotBlank(message = "Il cognome è obbligatorio")
    private String surname;

    @NotBlank(message = "Il numero di telefono è obbligatorio")
    private String cell;

    // Getter e Setter
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getCell() {
		return cell;
	}
	public void setCell(String cell) {
		this.cell = cell;
	}
}
