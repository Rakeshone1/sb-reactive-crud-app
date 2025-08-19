package com.wipro.controller;




import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.wipro.entity.User;
import com.wipro.service.UserServiceSSE;


import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;




@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserControllerSSE {
    private final UserServiceSSE userService;




    @GetMapping
    public Flux<User> getAllUsers() {
        return userService.getAllUsers();
    }




    @GetMapping("/{id}")
    public Mono<User> getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }




    @PostMapping
    public Mono<User> createUser(@RequestBody User user) {
        return userService.createUser(user);
    }




    @PutMapping("/{id}")
    public Mono<User> updateUser(@PathVariable Integer id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }




    @DeleteMapping("/{id}")
    public Mono<Void> deleteUser(@PathVariable Integer id) {
        return userService.deleteUser(id);
    }




    // SSE endpoint: clients connect here to receive real-time User events
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<User> streamUsers() {
        return userService.userEvents();
    }
}

