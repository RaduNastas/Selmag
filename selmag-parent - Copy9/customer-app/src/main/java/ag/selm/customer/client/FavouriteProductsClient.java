package ag.selm.customer.client;

import ag.selm.customer.entity.FavouriteProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



public interface FavouriteProductsClient {

    Mono<FavouriteProduct> findFavouriteProductByProductId(int productId);

     Mono<FavouriteProduct> addProductToFavourites(int productId);

     Mono<Void> removeProductFromFavourites(int productId);

    Flux<FavouriteProduct> findFavouriteProducts();


}
