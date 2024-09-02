package ag.selm.customer.controller.payload;


public record NewProductReviewPayload(Integer productId, Integer rating, String review) {
}
