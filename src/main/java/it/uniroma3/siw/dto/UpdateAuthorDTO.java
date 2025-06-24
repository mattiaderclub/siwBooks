package it.uniroma3.siw.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;

public class UpdateAuthorDTO {

	private Long id;

	@NotBlank(message = "Il nome è obbligatorio")
	private String name;

	@NotBlank(message = "Il cognome è obbligatorio")
	private String surname;

	@PastOrPresent(message = "La data di nascita non può essere futura")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate birthDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate deathDate;

	@NotBlank(message = "La nazionalità è obbligatoria")
	private String nationality;

	// Path attuale dell'immagine (se presente)
	private String photoPath;

	// Flag per eliminare l’immagine esistente
	private boolean deletePhoto;

	// === Getter & Setter ===

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public LocalDate getDeathDate() {
		return deathDate;
	}

	public void setDeathDate(LocalDate deathDate) {
		this.deathDate = deathDate;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getPhotoPath() {
		return photoPath;
	}

	public void setPhotoPath(String photoPath) {
		this.photoPath = photoPath;
	}

	public boolean isDeletePhoto() {
		return deletePhoto;
	}

	public void setDeletePhoto(boolean deletePhoto) {
		this.deletePhoto = deletePhoto;
	}
}
