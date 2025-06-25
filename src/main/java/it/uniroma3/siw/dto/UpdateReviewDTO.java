package it.uniroma3.siw.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdateReviewDTO {

	private Long id; // null per la creazione, valorizzato per la modifica

	@NotBlank(message = "{review.title.notblank}")
	private String title;

	@NotNull(message = "{review.rating.notnull}")
	@Min(1)
	@Max(5)
	private Integer rating;

	@NotBlank(message = "{review.content.notblank}")
	private String text;

	private Long bookId;

	// Getter e Setter
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

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Long getBookId() {
		return bookId;
	}

	public void setBookId(Long bookId) {
		this.bookId = bookId;
	}
}
