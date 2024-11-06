package Webflux.service;

import Webflux.model.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {

    private final WebClient webClient;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public Mono<User> getUserById(Long id) {
        return webClient.get()
                .uri("/api/users/{id}", id)
                .retrieve()
                .bodyToMono(User.class)
                .doOnError(e -> logger.error("Error fetching user with ID {}: {}", id, e.getMessage()))
                .onErrorResume(e -> Mono.empty()); // Handle errors gracefully
    }

    public Flux<User> getAllUsers() {
        return webClient.get()
                .uri("/api/users")
                .retrieve()
                .bodyToFlux(User.class)
                .doOnError(e -> logger.error("Error fetching all users: {}", e.getMessage()));
    }

    public Mono<User> createUser(User user) {
        if (user == null) {
            // Log an error or throw an exception
            throw new IllegalArgumentException("User object cannot be null");
        }
        return webClient.post()
                .uri("/api/users")
                .bodyValue(user)
                .retrieve()
                .bodyToMono(User.class);
    }


    public Mono<User> updateUser(Long id, User user) {
        return webClient.put()
                .uri("/api/users/{id}", id)
                .bodyValue(user)
                .retrieve()
                .bodyToMono(User.class)
                .onErrorResume(e -> {
                    // Log the error or handle it accordingly
                    return Mono.empty(); // Return an empty Mono on error
                });
    }


//    public Mono<ResponseEntity<Void>> deleteUser(Long id) {
//        return webClient.delete()
//                .uri("/api/users/{id}", id)
//                .retrieve()
//                .bodyToMono(Void.class)
//                .map(v -> ResponseEntity.noContent().build()) // Return 204 No Content on success
//                .onErrorResume(e -> {
//                    logger.error("Error deleting user with ID {}: {}", id, e.getMessage());
//                    return Mono.just(ResponseEntity.notFound().build()); // Return 404 if not found
//                });
//    }
}
