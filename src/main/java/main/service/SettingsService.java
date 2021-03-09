package main.service;

import main.api.response.InitResponse;
import main.api.response.SettingsResponse;
import main.exception.ContentNotFoundException;
import main.model.GlobalSettings;
import main.repository.GlobalSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Класс общих настроек блога
 */
@Service
public class SettingsService {
    @Value("${settings.multiuser-mode}")
    private String multiUserMode;
    @Value("${settings.post-premoderation}")
    private String postPreModeration;
    @Value("${settings.statistics-is-public}")
    private String statisticsIsPublic;
    @Value("${settings.on-value}")
    private String onValue;
    @Value("${settings.off-value}")
    private String offValue;

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
            generateAndSave(multiUserMode, "Многопользовательский режим", onValue);
            generateAndSave(postPreModeration, "Премодерация постов", onValue);
            generateAndSave(statisticsIsPublic, "Показывать всем статистику блога", onValue);
        }
    }

    /**
     * Метод проверяет включена ли премодерация постов
     * @return true, если включена или false, если отключена
     */
    public boolean preModerationIsOn() {
        return settingsRepository.existsByCodeAndValueIgnoreCase(postPreModeration, onValue);
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
    public SettingsResponse getSettings() {
        GlobalSettings multiUserMode = findByCode(this.multiUserMode);
        GlobalSettings postPreModeration = findByCode(this.postPreModeration);
        GlobalSettings statisticsIsPublic = findByCode(this.statisticsIsPublic);

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
    public GlobalSettings findByCode(String code) {
        return settingsRepository.findByCode(code)
                .orElseThrow(() -> new ContentNotFoundException("Настройка с кодом " + code + " не найдена"));
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
    public void saveSettings(SettingsResponse response) {
        updateSetting(multiUserMode, response.isMultiUserMode());
        updateSetting(postPreModeration, response.isPostPreModeration());
        updateSetting(statisticsIsPublic, response.isStatisticsIsPublic());
    }

    /**
     * Метод обновления значения настройки
     *
     * @param code - код настройки
     * @param value - значение настройки
     * @throws ContentNotFoundException в случае, если настройка не найдена
     */
    private void updateSetting(String code, boolean value) {
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
        return settingsRepository.existsByCodeAndValueIgnoreCase(statisticsIsPublic, "YES");
    }
}
