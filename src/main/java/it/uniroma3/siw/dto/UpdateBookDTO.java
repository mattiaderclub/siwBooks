package it.uniroma3.siw.dto;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdateBookDTO {

	private Long id;

	@NotBlank(message = "Il titolo è obbligatorio")
	private String title;

	@NotNull(message = "L'anno di pubblicazione è obbligatorio")
	@Min(value = 0, message = "L'anno deve essere positivo")
	private Integer annoPubblicazione;

	// Percorsi delle immagini già salvate
	private List<String> imagePaths;

	// Percorsi delle immagini da eliminare
	private List<String> imagesToDelete;

	// Ordine delle immagini aggiornato dal form
	private String[] imageOrder;

	// === GETTER e SETTER ===

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getAnnoPubblicazione() {
		return annoPubblicazione;
	}

	public void setAnnoPubblicazione(Integer annoPubblicazione) {
		this.annoPubblicazione = annoPubblicazione;
	}

	public List<String> getImagePaths() {
		return imagePaths;
	}

	public void setImagePaths(List<String> imagePaths) {
		this.imagePaths = imagePaths;
	}

	public List<String> getImagesToDelete() {
		return imagesToDelete;
	}

	public void setImagesToDelete(List<String> imagesToDelete) {
		this.imagesToDelete = imagesToDelete;
	}

	public String[] getImageOrder() {
		return imageOrder;
	}

	public void setImageOrder(String[] imageOrder) {
		this.imageOrder = imageOrder;
	}
}
