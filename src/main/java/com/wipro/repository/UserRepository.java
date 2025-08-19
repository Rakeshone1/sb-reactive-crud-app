
package com.wipro.repository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;


import com.wipro.entity.User;


@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Integer> {
}