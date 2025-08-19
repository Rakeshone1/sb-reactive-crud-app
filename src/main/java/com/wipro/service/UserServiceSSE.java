package com.wipro.service;




import com.wipro.entity.User;
import com.wipro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;


/*
 * Sinks.many().multicast().onBackpressureBuffer() is a common pattern for broadcasting events to 
 * multiple subscribers and buffering when clients are slow.




tryEmitNext(...) is used instead of emitNext(...) to avoid throwing exceptions on 
emission failures (you can inspect the returned Sinks.EmitResult if you want to handle failures explicitly).


 */
@Service
@RequiredArgsConstructor
public class UserServiceSSE {
    private final UserRepository userRepository;




    // Multicast sink: emits new User events to multiple subscribers.
    // onBackpressureBuffer() behavior is provided by the sink configuration used below.
    private final Sinks.Many<User> userSink = Sinks.many().multicast().onBackpressureBuffer();




    // Expose a flux view of the sink for subscribers.
    public Flux<User> userEvents() {
        return userSink.asFlux();
    }




    public Flux<User> getAllUsers() {
        return userRepository.findAll();
    }




    public Mono<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }




    public Mono<User> createUser(User user) {
        // Save to DB, then emit to sink (non-blocking)
        return userRepository.save(user)
                .doOnSuccess(savedUser -> {
                    if (savedUser != null) {
                        userSink.tryEmitNext(savedUser);
                    }
                });
    }




    public Mono<User> updateUser(Integer id, User user) {
        return userRepository.findById(id)
                .flatMap(existingUser -> {
                    existingUser.setName(user.getName());
                    existingUser.setEmail(user.getEmail());
                    return userRepository.save(existingUser);
                });
    }




    public Mono<Void> deleteUser(Integer id) {
        return userRepository.deleteById(id);
    }
}



