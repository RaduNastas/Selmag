package ag.selm.catalogue.service;

import ag.selm.catalogue.entity.Product;
import ag.selm.catalogue.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    DefaultProductService service;

    @Test
    void findAllProducts_FilterIsNotSet_ReturnsProductsList() {
        // given
        var products = IntStream.range(1, 4)
                .mapToObj(i -> new Product(i, "Product N%d".formatted(i), "Product description N%d".formatted(i)))
                .toList();

        doReturn(products).when(this.productRepository).findAll();

        // when
        var result = this.service.findAllProducts(null);

        // then
        assertEquals(products, result);

        verify(this.productRepository).findAll();
        verifyNoMoreInteractions(this.productRepository);
    }

    @Test
    void findAllProducts_FilterIsSet_ReturnsFilteredProductsList() {
        // given
        var products = IntStream.range(1, 4)
                .mapToObj(i -> new Product(i, "Product N%d".formatted(i), "Product description N%d".formatted(i)))
                .toList();

        doReturn(products).when(this.productRepository).findAllByTitleLikeIgnoreCase("%product%");

        // when
        var result = this.service.findAllProducts("product");

        // then
        assertEquals(products, result);

        verify(this.productRepository).findAllByTitleLikeIgnoreCase("%product%");
        verifyNoMoreInteractions(this.productRepository);
    }

    @Test
    void findProduct_ProductExists_ReturnsNotEmptyOptional() {
        // given
        var product = new Product(1, "Product N1", "Product description N1");

        doReturn(Optional.of(product)).when(this.productRepository).findById(1);

        // when
        var result = this.service.findProduct(1);

        // then
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(product, result.orElseThrow());

        verify(this.productRepository).findById(1);
        verifyNoMoreInteractions(this.productRepository);
    }

    @Test
    void findProduct_ProductDoesNotExist_ReturnsEmptyOptional() {
        // given
        var product = new Product(1, "Product N1", "Product description N1");

        // when
        var result = this.service.findProduct(1);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(this.productRepository).findById(1);
        verifyNoMoreInteractions(this.productRepository);
    }

    @Test
    void createProduct_ReturnsCreatedProduct() {
        // given
        var title = "New product";
        var details = "New product description";

        doReturn(new Product(1, "New product", "New product description"))
                .when(this.productRepository).save(new Product(null, "New product", "New product description"));

        // when
        var result = this.service.createProduct(title, details);

        // then
        assertEquals(new Product(1, "New product", "New product description"), result);

        verify(this.productRepository).save(new Product(null, "New product", "New product description"));
        verifyNoMoreInteractions(this.productRepository);
    }

    @Test
    void updateProduct_ProductExists_UpdatesProduct() {
        // given
        var productId = 1;
        var product = new Product(1, "New product", "New product description");
        var title = "New name";
        var details = "New description";

        doReturn(Optional.of(product))
                .when(this.productRepository).findById(1);

        // when
        this.service.updateProduct(productId, title, details);

        // then
        verify(this.productRepository).findById(productId);
        verifyNoMoreInteractions(this.productRepository);
    }

    @Test
    void updateProduct_ProductDoesNotExist_ThrowsNoSuchElementException() {
        // given
        var productId = 1;
        var title = "New name";
        var details = "New description";

        // when
        assertThrows(NoSuchElementException.class, () -> this.service
                .updateProduct(productId, title, details));

        // then
        verify(this.productRepository).findById(productId);
        verifyNoMoreInteractions(this.productRepository);
    }

    @Test
    void deleteProduct_DeletesProduct() {
        // given
        var productId = 1;

        // when
        this.service.deleteProduct(productId);

        // then
        verify(this.productRepository).deleteById(productId);
        verifyNoMoreInteractions(this.productRepository);
    }
}