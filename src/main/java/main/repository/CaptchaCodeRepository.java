package main.repository;

import main.model.CaptchaCode;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CaptchaCodeRepository extends CrudRepository<CaptchaCode, Integer> {

    @Query(value = "from CaptchaCode c where c.time < ?1")
    List<CaptchaCode> findAllExpiredCodes(@Param("expTime") LocalDateTime expirationTime);

    Optional<CaptchaCode> findBySecretCode(String secretCode);

    @Transactional
    @Modifying
    @Query(value = "delete from CaptchaCode c where c.time < ?1")
    void deleteByTime(@Param("expTime") LocalDateTime time);
}
