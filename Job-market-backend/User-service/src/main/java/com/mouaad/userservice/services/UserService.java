package com.mouaad.userservice.services;

import com.mouaad.userservice.repositories.ReviewRepository;
import com.mouaad.userservice.repositories.UserRepository;
import com.mouaad.userservice.dtos.CreateReviewRequest;
import com.mouaad.userservice.dtos.CreateUserProfileRequest;
import com.mouaad.userservice.dtos.ReviewDto;
import com.mouaad.userservice.dtos.UpdateProfileRequest;
import com.mouaad.userservice.dtos.UserDto;
import com.mouaad.userservice.entities.Review;
import com.mouaad.userservice.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public UserDto createProfile(CreateUserProfileRequest request) {
        User user = new User();
        user.setId(request.getId());
        user.setFullName(request.getFullName());
        user.setBio(request.getBio());
        user.setSkills(request.getSkills());
        user.setProfileImageUrl(request.getProfileImageUrl());
        user.setRatingAvg(BigDecimal.ZERO);
        user.setRatingCount(0);

        User saved = userRepository.save(user);
        return toDto(saved);
    }

    public UserDto getById(UUID id) {
        return userRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id));
    }

    @Transactional
    public UserDto updateProfile(UUID id, UpdateProfileRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id));

        // Your excellent null checks for partial updates
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getSkills() != null) {
            user.setSkills(request.getSkills());
        }
        if (request.getProfileImageUrl() != null) {
            user.setProfileImageUrl(request.getProfileImageUrl());
        }
        user.setUpdatedAt(LocalDateTime.now());

        User saved = userRepository.save(user);
        return toDto(saved);
    }

    public List<ReviewDto> getReviewsForUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId);
        }

        return reviewRepository.findByRevieweeId(userId).stream()
                .map(this::toReviewDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewDto addReview(UUID revieweeId, CreateReviewRequest request) {
        if (reviewRepository.existsByJobIdAndRevieweeId(request.getJobId(), revieweeId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A review for this job and user already exists");
        }

        userRepository.findById(revieweeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + revieweeId));

        Review review = new Review();
        review.setJobId(request.getJobId());
        review.setReviewerId(request.getReviewerId());
        review.setRevieweeId(revieweeId);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review saved = reviewRepository.save(review);
        recalculateRating(revieweeId);
        return toReviewDto(saved);
    }

    @Transactional
    protected void recalculateRating(UUID userId) {
        List<Review> reviews = reviewRepository.findByRevieweeId(userId);

        BigDecimal average = reviews.isEmpty()
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(reviews.stream().mapToInt(Review::getRating).average().orElse(0.0))
                .setScale(2, RoundingMode.HALF_UP);

        userRepository.findById(userId).ifPresent(user -> {
            user.setRatingAvg(average);
            user.setRatingCount(reviews.size());
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        });
    }

    private UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setBio(user.getBio());
        dto.setSkills(user.getSkills());
        dto.setRatingAvg(user.getRatingAvg());
        dto.setRatingCount(user.getRatingCount());
        dto.setStripeAccountId(user.getStripeAccountId());
        dto.setProfileImageUrl(user.getProfileImageUrl());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }

    private ReviewDto toReviewDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        dto.setJobId(review.getJobId());
        dto.setReviewerId(review.getReviewerId());
        dto.setRevieweeId(review.getRevieweeId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }
}