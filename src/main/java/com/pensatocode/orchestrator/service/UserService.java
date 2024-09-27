package com.pensatocode.orchestrator.service;

import com.pensatocode.orchestrator.exception.UsernameTakenException;
import com.pensatocode.orchestrator.model.User;
import com.pensatocode.orchestrator.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Flux<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Mono<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Mono<User> createUser(User user) {
        return userRepository.findByUsername(user.getUsername())
                .flatMap(existingUser -> Mono.<User>error(new UsernameTakenException("Username '" + user.getUsername() + "' is already taken")))
                .switchIfEmpty(Mono.defer(() -> {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    return userRepository.save(user);
                }));
    }

    public Mono<User> updateUser(Long id, User user) {
        return userRepository.findById(id)
                .flatMap(existingUser -> {
                    existingUser.setFirstName(user.getFirstName());
                    existingUser.setLastName(user.getLastName());
                    existingUser.setEmail(user.getEmail());
                    if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
                    }
                    return userRepository.save(existingUser);
                });
    }

    public Mono<Void> deleteUser(Long id) {
        return userRepository.deleteById(id);
    }

    public Mono<Long> getUserCount() {
        return userRepository.count();
    }

    public Mono<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Mono<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Mono<Boolean> verifyPassword(String rawPassword, String encodedPassword) {
        return Mono.fromCallable(() -> passwordEncoder.matches(rawPassword, encodedPassword));
    }
}
