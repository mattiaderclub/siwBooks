package it.uniroma3.siw.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Entity
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable = false)
    private String title;
    @Column(nullable = false)
    @Min(1)
    @Max(5)
    private int rating;
    @Column(nullable = false)
    private String text;
    
    @OneToOne
    private Book libro;
    @OneToOne
    private User user;
    
    // === Getter e Setter ===
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
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Book getLibro() {
		return libro;
	}
	public void setLibro(Book libro) {
		this.libro = libro;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, libro, rating, text, title, user);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Review other = (Review) obj;
		return Objects.equals(id, other.id) && Objects.equals(libro, other.libro) && rating == other.rating
				&& Objects.equals(text, other.text) && Objects.equals(title, other.title)
				&& Objects.equals(user, other.user);
	}
}
