package it.uniroma3.siw.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.ReviewRepository;

@Service
public class ReviewService {

	@Autowired
	private ReviewRepository reviewRepository;

	public Iterable<Review> getAllReviews() {
		return reviewRepository.findAll();
	}

	public Review getReviewById(Long id) {
		return reviewRepository.findById(id).orElse(null);
	}

	public void saveReview(Review review) {
		reviewRepository.save(review);
	}

	public void deleteById(Long id) {
		reviewRepository.deleteById(id);
	}

	public boolean existsByUserAndBook(User user, Book book) {
		return reviewRepository.existsByUserAndLibro(user, book);
	}

	public List<Review> getReviewsByBook(Book book) {
		return reviewRepository.findByLibro(book);
	}

	public Double getAverageRatingForBook(Book book) {
		return reviewRepository.averageRatingByLibro(book);
	}

	public Review findByUserAndBook(User user, Book book) {
		return reviewRepository.findByUserAndLibro(user, book).orElse(null);
	}
}
