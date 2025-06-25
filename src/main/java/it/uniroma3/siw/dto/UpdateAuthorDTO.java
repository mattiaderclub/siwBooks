package it.uniroma3.siw.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;

/**
 * DTO per aggiornare i dati di un autore.
 * 
 * Questo oggetto viene usato nei form per la modifica dell'autore. Contiene
 * solo i campi necessari alla vista e supporta validazione e binding.
 */
public class UpdateAuthorDTO {

	// Identificativo dell'autore da aggiornare (non modificabile nel form)
	private Long id;

	// Nome obbligatorio - non può essere vuoto
	@NotBlank(message = "Il nome è obbligatorio")
	private String name;

	// Cognome obbligatorio - non può essere vuoto
	@NotBlank(message = "Il cognome è obbligatorio")
	private String surname;

	// Data di nascita - obbligatoria e non può essere futura
	@PastOrPresent(message = "La data di nascita non può essere futura")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate birthDate;

	// Data di morte - facoltativa
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate deathDate;

	// Nazionalità obbligatoria
	@NotBlank(message = "La nazionalità è obbligatoria")
	private String nationality;

	// Percorso (URL relativo) della foto attuale dell'autore, usato nella vista per
	// mostrare l'immagine
	private String photoPath;

	// Indica se la foto esistente va rimossa (checkbox nella vista)
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
