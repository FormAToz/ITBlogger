package main.repository;

import main.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByIdAndEmailIgnoreCase(int id, String email);

    Optional<User> findByEmailIgnoreCase(String email);
}
