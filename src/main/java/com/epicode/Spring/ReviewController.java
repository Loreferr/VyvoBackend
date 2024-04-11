package com.epicode.Spring;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epicode.Spring.classes.Review;
import com.epicode.Spring.repo.ReviewRepo;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

	@Autowired
	private ReviewRepo reviewRepository;

	@GetMapping("")
	public List<Review> getAllReviews() {
		return reviewRepository.findAll();
	}

	@GetMapping("/{city}")
	@CrossOrigin(origins = "http://localhost:4200")
	public List<Review> getReviewsByCity(@PathVariable String city) {
		// Eseguire una query per recuperare le recensioni in base alla città
		List<Review> reviews = reviewRepository.findByCity(city);
		return reviews;
	}

	@GetMapping("/byAuthor/{author}")
	@CrossOrigin(origins = "http://localhost:4200")
	public List<Review> getReviewsByAuthor(@PathVariable String author) {
		return reviewRepository.findByAuthor(author);
	}

	@PostMapping("/add")
	@CrossOrigin(origins = "http://localhost:4200")
	public Review addReview(@RequestBody Review review) {
		Review savedReview = reviewRepository.save(review);
		return savedReview;
	}

	@DeleteMapping("/{id}")
	@CrossOrigin(origins = "http://localhost:4200")
	public ResponseEntity<String> deleteReview(@PathVariable Long id) {
		try {
			reviewRepository.deleteById(id);
			return ResponseEntity.ok("Recensione eliminata con successo");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Errore durante l'eliminazione della recensione");
		}
	}

	@PutMapping("/{id}")
	@CrossOrigin(origins = "http://localhost:4200")
	public ResponseEntity<Review> updateReview(@PathVariable Long id, @RequestBody Review updatedReview) {
		Optional<Review> existingReview = reviewRepository.findById(id);

		if (existingReview.isPresent()) {
			Review reviewToUpdate = existingReview.get();
			reviewToUpdate.setCity(updatedReview.getCity());
			reviewToUpdate.setDescription(updatedReview.getDescription());
			reviewToUpdate.setRating(updatedReview.getRating());

			Review savedReview = reviewRepository.save(reviewToUpdate);
			return ResponseEntity.ok(savedReview);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

}
