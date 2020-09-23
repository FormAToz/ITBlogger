package main.service;

import main.api.response.InitResponse;
import main.api.response.SettingsResponse;
import main.exception.SettingNotFoundException;
import main.model.GlobalSettings;
import main.repository.GlobalSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Класс общих настроек блога
 */
@Service
public class SettingsService {
    private static final String MULTIUSER_MODE = "MULTIUSER_MODE";
    private static final String POST_PREMODERATION = "POST_PREMODERATION";
    private static final String STATISTICS_IS_PUBLIC = "STATISTICS_IS_PUBLIC";
    private static final String DEFAULT_VALUE = "YES";

    @Autowired
    private GlobalSettingsRepository settingsRepository;

    /**
     * Метод возвращает общую информацию о блоге: название блога и подзаголовок для размещения в хэдере сайта,
     * а также номер телефона, e-mail и информацию об авторских правах для размещения в футере.
     */
    public InitResponse init() {
        return new InitResponse()
                .title("IT-Blogger")
                .subtitle("Рассказы разработчиков")
                .phone("+7 999 777-77-77")
                .email("7.danilov@gmail.com")
                .copyright("Андрей Данилов")
                .copyrightFrom("2020");
    }

    /**
     * Метод проверяет наличие настроек в репозитории и сохраняет их при отсутствии
     * Все значения при генерации по умолчанию - "YES"
     */
    @PostConstruct
    private void initSettings() {
        if (settingsRepository.count() == 0) {
            generateAndSave(MULTIUSER_MODE, "Многопользовательский режим", DEFAULT_VALUE);
            generateAndSave(POST_PREMODERATION, "Премодерация постов", DEFAULT_VALUE);
            generateAndSave(STATISTICS_IS_PUBLIC, "Показывать всем статистику блога", DEFAULT_VALUE);
        }
    }

    /**
     * Метод генерации и сохранения настроек блога
     */
    private void generateAndSave(String code, String name, String value) {
        settingsRepository.save(new GlobalSettings(code, name, value));
    }

    /**
     * Метод возвращает глобальные настройки блога из таблицы global_settings.
     *
     * @return SettingsResponse
     */
    public SettingsResponse getSettings() throws SettingNotFoundException {
        GlobalSettings multiUserMode = findByCode(MULTIUSER_MODE);
        GlobalSettings postPreModeration = findByCode(POST_PREMODERATION);
        GlobalSettings statisticsIsPublic = findByCode(STATISTICS_IS_PUBLIC);

        return new SettingsResponse(stringToBoolean(multiUserMode.getValue()),
                stringToBoolean(postPreModeration.getValue()),
                stringToBoolean(statisticsIsPublic.getValue()));
    }

    /**
     * Метод преобразования String в boolean
     *
     * @param string - значение настройки
     * @return true или false
     */
    private boolean stringToBoolean(String string) {
        return string.equalsIgnoreCase("YES");
    }

    /**
     * Метод поиска настройки по коду
     *
     * @param code - название кода
     * @return GlobalSettings
     */
    public GlobalSettings findByCode(String code) throws SettingNotFoundException {
        return settingsRepository.findByCode(code)
                .orElseThrow(() -> new SettingNotFoundException("Настройка с кодом " + code + " не найдена"));
    }

    /**
     * Метод записывает глобальные настройки блога в таблицу global_settings,
     * если запрашивающий пользователь авторизован и является модератором.
     *
     * Значения глобальных настроек
     *      *
     * code                  name                               value
     * MULTIUSER_MODE        Многопользовательский режим        YES/NO
     * POST_PREMODERATION    Премодерация постов                YES/NO
     * STATISTICS_IS_PUBLIC  Показывать всем статистику блога   YES/NO
     *
     * @param response - запрос с фронта
     */
    public void saveSettings(SettingsResponse response) throws SettingNotFoundException {
        updateSetting(MULTIUSER_MODE, response.isMultiUserMode());
        updateSetting(POST_PREMODERATION, response.isPostPreModeration());
        updateSetting(STATISTICS_IS_PUBLIC, response.isStatisticsIsPublic());
    }

    /**
     * Метод обновления значения настройки
     *
     * @param code - код настройки
     * @param value - значение настройки
     * @throws SettingNotFoundException в случае, если настройка не найдена
     */
    private void updateSetting(String code, boolean value) throws SettingNotFoundException {
        GlobalSettings setting = findByCode(code);
        setting.setValue(booleanToString(value));
        settingsRepository.save(setting);
    }

    /**
     * Метод преобразования boolean в String
     * @param b - true или false
     * @return - YES или NO
     */
    private String booleanToString(boolean b) {
        return b ? "YES" : "NO";
    }

    /**
     * Метод проверяет разрешен ли показ статистики всего блога.
     *
     * @return true - разрешен, false - запрещен
     */
    public boolean globalStatisticsIsAvailable() {
        return settingsRepository.existsByCodeAndValueIgnoreCase(STATISTICS_IS_PUBLIC, "YES");
    }
}
