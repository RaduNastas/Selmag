package ag.selm.feedback.repository;

import ag.selm.feedback.entity.ProductReview;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;


import java.util.UUID;

public interface ProductReviewRepository extends
        ReactiveCrudRepository<ProductReview, UUID> {
    @Query("{'productId': ?0}")
    Flux<ProductReview> findAllByProductId(int productId);
}
