package main.repository;

import main.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByIdAndEmailIgnoreCase(long id, String email);

    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> getUserByCode(String code);
}
