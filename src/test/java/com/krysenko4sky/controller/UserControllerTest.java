package com.krysenko4sky.controller;

import com.krysenko4sky.model.dto.UserDto;
import com.krysenko4sky.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(UserController.class)
@Import(TestSecurityConfig.class)
class UserControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserService userService;

    private final UUID userId = UUID.randomUUID();
    private final String username = "Test User";
    private final UserDto userDto = UserDto.builder().username(username).id(userId).build();

    @Test
    void testCreateUser() {
        when(userService.createUser(any(UserDto.class))).thenReturn(Mono.just(userDto));
        webTestClient.post().uri(UserController.USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"username\": \"Test User\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDto.class)
                .value(user -> {
                    assert user != null;
                    assert user.getId().equals(userId);
                    assert user.getUsername().equals(username);
                });
        Mockito.verify(userService).createUser(any(UserDto.class));
    }

    @Test
    void testGetUserById() {
        when(userService.getUserById(eq(userId))).thenReturn(Mono.just(userDto));
        webTestClient.get().uri(UserController.USERS + "/{id}", userId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDto.class)
                .value(user -> {
                    assert user != null;
                    assert user.getId().equals(userId);
                    assert user.getUsername().equals(username);
                });
        Mockito.verify(userService).getUserById(eq(userId));
    }

    @Test
    void testUpdateUser() {
        when(userService.updateUser(eq(userId), any(UserDto.class))).thenReturn(Mono.just(userDto));
        webTestClient.put().uri(UserController.USERS + "/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"username\": \"New Test User\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDto.class)
                .value(user -> {
                    assert user != null;
                    assert user.getId().equals(userId);
                    assert user.getUsername().equals(username);
                });
        Mockito.verify(userService).updateUser(eq(userId), any(UserDto.class));
    }

    @Test
    void testDeleteUser() {
        UUID userId = UUID.randomUUID();
        when(userService.deleteUser(eq(userId))).thenReturn(Mono.empty());
        webTestClient.delete().uri(UserController.USERS + "/{id}", userId)
                .exchange()
                .expectStatus().isOk();
        Mockito.verify(userService).deleteUser(eq(userId));
    }

    @Test
    void testGetAllUsers() {

        when(userService.getAllUsers()).thenReturn(Flux.just(userDto));

        webTestClient.get().uri("/users")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserDto.class)
                .value(users -> {
                    assert users.size() == 1;
                    assert users.getFirst().getId().equals(userId);
                    assert users.getFirst().getUsername().equals(username);
                });
        Mockito.verify(userService).getAllUsers();
    }
}