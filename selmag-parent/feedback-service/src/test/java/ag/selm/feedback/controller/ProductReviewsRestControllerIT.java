package ag.selm.feedback.controller;

import ag.selm.feedback.entity.ProductReview;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@Slf4j
@SpringBootTest
@AutoConfigureWebTestClient
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class ProductReviewsRestControllerIT {


    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    @BeforeEach
    void setUp() {
        this.reactiveMongoTemplate.insertAll(List.of(
                new ProductReview(UUID.fromString("0335d1b0-723d-4183-8b66-b07596334f49"),
                        1, 1, "Review N1", "user-1"),
                new ProductReview(UUID.fromString("efb4d7f0-05f4-4449-a5e6-d07d8deb0277"),
                        1, 3, "Review N2", "user-2"),
                new ProductReview(UUID.fromString("09f6a2c4-8552-42c5-8dfb-1b2f6171a7ec"),
                        1, 5, "Review N3", "user-3")
        )).blockLast();

    }

    @AfterEach
    void tearDown() {
        this.reactiveMongoTemplate.remove(ProductReview.class).all().block();
    }

    @Test
    void findProductReviewsByProductId_ReturnsReviews() {
        //when


        // then
        this.webTestClient.mutateWith(mockJwt())
                .mutate().filter(ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
                    log.info("=========== REQUEST ==========");
                    log.info("{} {}", clientRequest.method(), clientRequest.url());
                    clientRequest.headers().forEach((header, value) -> log.info("{}: {}", header, value));
                    log.info("======= END REQUEST =========");
                    return Mono.just(clientRequest);
                }))
                .build()
                .get()
                .uri("/feedback-api/product-reviews/by-product-id/1")
                .exchange()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("""
                        [
                            {
                                "id": "0335d1b0-723d-4183-8b66-b07596334f49",
                                "productId": 1,
                                "rating": 1,
                                "review": "Review N1",
                                "userId": "user-1"
                            },
                        
                            {
                                "id": "efb4d7f0-05f4-4449-a5e6-d07d8deb0277",
                                "productId": 1,
                                "rating": 3,
                                "review": "Review N2",
                                "userId": "user-2"
                            },
                        
                            {
                                "id": "09f6a2c4-8552-42c5-8dfb-1b2f6171a7ec",
                                 "productId": 1,
                                 "rating": 5,
                                 "review": "Review N3",
                                 "userId": "user-3"}
                        ]""");
    }

    @Test
    void createProductReview_RequestIsValid_ReturnsCreatedProductReview() {
        // given
        // when
        this.webTestClient
                .mutateWith(mockJwt().jwt(builder -> builder.subject("user-tester")))
                .post()
                .uri("/feedback-api/product-reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                        "productId": 1,
                        "rating": 5,
                        "review": "Five!"
                        }""")
        // then
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .json(
                        """
                        {
                                "productId": 1,
                                "rating": 5,
                                "review": "Five!",
                                "userId": "user-tester"

                        }""").jsonPath("$.id").exists()
                .consumeWith(document("feedback/product_reviews/create_product_review",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("productId").type("int").description("Product ID"),
                                fieldWithPath("rating").type("int").description("Evaluation"),
                                fieldWithPath("review").type("string").description("Review")
                        ),
                        responseFields(
                                fieldWithPath("id").type("uuid").description("Review ID"),
                                fieldWithPath("productId").type("int").description("Product ID"),
                                fieldWithPath("rating").type("int").description("Evaluation"),
                                fieldWithPath("review").type("string").description("Review"),
                                fieldWithPath("userId").type("string").description("User ID")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION)
                                        .description("A link to the product review created")
                        )));

    }
    @Test
    void createProductReview_RequestIsInvalid_ReturnsBadRequest() {
        // given
        // when
        this.webTestClient
                .mutateWith(mockJwt().jwt(builder -> builder.subject("user-tester")))
                .post()
                .uri("/feedback-api/product-reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                        "productId": null,
                        "rating": -1,
                        "review": "Five! În timp ce Windows PowerShell (versiunile 5.1 și anterioare) este preinstalat pe Windows, PowerShell Core (începând cu versiunea 6 și ulterior 7) este o instalație separată și coexiste cu Windows PowerShell. PowerShell CoreÎn timp ce Windows PowerShell (versiunile 5.1 și anterioare) este preinstalat pe Windows, PowerShell Core (începând cu versiunea 6 și ulterior 7) este o instalație separată și coexiste cu Windows PowerShell. PowerShell CoreÎn timp ce Windows PowerShell (versiunile 5.1 și anterioare) este preinstalat pe Windows, PowerShell Core (începând cu versiunea 6 și ulterior 7) este o instalație separată și coexiste cu Windows PowerShell. PowerShell CoreÎn timp ce Windows PowerShell (versiunile 5.1 și anterioare) este preinstalat pe Windows, PowerShell Core (începând cu versiunea 6 și ulterior 7) este o instalație separată și coexiste cu Windows PowerShell. PowerShell Core este preferabil datorită suportului continuu și a îmbunătățirilor de performanță și securitate. este preferabil datorită suportului continuu și a îmbunătățirilor de performanță și securitate. este preferabil datorită suportului continuu și a îmbunătățirilor de performanță și securitate. este preferabil datorită suportului continuu și a îmbunătățirilor de performanță și securitate.IntelliJ IDEA oferă suport  În timp ce Windows PowerShell (versiunile 5.1 și anterioare) este preinstalat pe Windows, PowerShell Core (începând cu versiunea 6 și ulterior 7) este o instalație separată și coexiste cu Windows PowerShell. PowerShell Core este preferabil datorită suportului continuu și a îmbunătățirilor de performanță și securitate.pentru formatarea codului, inclusiv JSON, dar este posibil ca uneori să fie necesar să configurezi corect setările pentru a obține formatul dorit. Dacă întâmpini dificultăți în formatarea codului JSON, iată câțiva pași pentru a te asigura că setările sunt corecte și pentru a folosi corect funcțiile de formatare."
                        }""")
                // then
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().doesNotExist(HttpHeaders.LOCATION)
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json(
                        """
                        {
                                "errors": [
                                    "Product not specified",
                                    "Estimate less 1",
                                    "The size of the review should not exceed 1000 characters"
                            ]

                        }""");
    }
    @Test
    void createProductReview_UserIsNotAuthenticated_ReturnsNotAuthorized() {
        // given

        // when
        this.webTestClient
                .post()
                .uri("/feedback-api/product-reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "productId": 1,
                            "rating": 5,
                            "review": "Five!"
                        }""")
                .exchange()
                // then
                .expectStatus().isUnauthorized();
    }
}