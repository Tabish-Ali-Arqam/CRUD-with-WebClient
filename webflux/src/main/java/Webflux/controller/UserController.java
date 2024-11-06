package Webflux.controller;

import Webflux.model.User;
import Webflux.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService; // Renamed for clarity
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/{id}")
    public Mono<ResponseEntity<User>> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .doOnSubscribe(s -> logger.info("Fetching user with ID: {}", id))
                .doOnError(e -> logger.error("Error fetching user with ID {}: {}", id, e.getMessage()));
    }

    @GetMapping
    public Flux<User> getAllUsers() {
        return userService.getAllUsers()
                .doOnSubscribe(s -> logger.info("Fetching all users"))
                .doOnError(e -> logger.error("Error fetching all users: {}", e.getMessage()));
    }

    @PostMapping
    public Mono<User> createUser(@RequestBody User user) {
        if (user == null) {
            // Handle the null case appropriately
            throw new IllegalArgumentException("User cannot be null");
        }
        return userService.createUser(user);
    }


    @PutMapping("/{id}")
    public Mono<ResponseEntity<User>> updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user)
                .map(updatedUser -> {
                    logger.info("Updated user with ID: {}", id);
                    return ResponseEntity.ok(updatedUser);
                })
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .doOnError(e -> logger.error("Error updating user with ID {}: {}", id, e.getMessage()));
    }

//    @DeleteMapping("/{id}")
//    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable Long id) {
//        return userService.deleteUser(id)
//                .then(Mono.just(ResponseEntity.noContent().build())) // Return 204 No Content on success
//                .doOnSubscribe(s -> logger.info("Deleting user with ID: {}", id))
//                .onErrorResume(e -> {
//                    logger.error("Error deleting user with ID {}: {}", id, e.getMessage());
//                    return Mono.just(ResponseEntity.notFound().build()); // Return 404 Not Found on error
//                });
//    }
}
