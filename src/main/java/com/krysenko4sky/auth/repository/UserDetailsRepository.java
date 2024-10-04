package com.krysenko4sky.auth.repository;

import com.krysenko4sky.auth.model.dao.UserDetails;
import com.krysenko4sky.model.dao.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserDetailsRepository extends ReactiveCrudRepository<UserDetails, UUID> {

    Mono<UserDetails> findByEmail(String email);
}
