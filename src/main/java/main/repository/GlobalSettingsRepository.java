package main.repository;

import main.model.GlobalSetting;
import main.model.enums.Setting;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GlobalSettingsRepository extends CrudRepository<GlobalSetting, Long> {

    Optional<GlobalSetting> findByCode(Setting code);

    boolean existsByCodeAndValueIgnoreCase(Setting code, String value);
}
