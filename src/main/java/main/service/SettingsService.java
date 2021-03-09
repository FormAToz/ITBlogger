package main.service;

import main.api.response.InitResponse;
import main.api.response.SettingsResponse;
import main.exception.ContentNotFoundException;
import main.model.GlobalSetting;
import main.model.enums.Setting;
import main.repository.GlobalSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;

/**
 * Класс общих настроек блога
 */
@Service
public class SettingsService {
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
            Arrays.stream(Setting.values())
                    .forEach(el -> settingsRepository.save(new GlobalSetting(el, el.getName(), onValue)));
        }
    }

    /**
     * Метод проверяет включена ли премодерация постов
     *
     * @return true, если включена или false, если отключена
     */
    public boolean preModerationIsOn() {
        return settingsRepository.existsByCodeAndValueIgnoreCase(Setting.POST_PREMODERATION, onValue);
    }

    /**
     * Метод возвращает глобальные настройки блога из таблицы global_settings.
     *
     * @return SettingsResponse
     */
    public SettingsResponse getSettings() {
        GlobalSetting multiUserMode = findByCode(Setting.MULTIUSER_MODE);
        GlobalSetting postPreModeration = findByCode(Setting.POST_PREMODERATION);
        GlobalSetting statisticsIsPublic = findByCode(Setting.STATISTICS_IS_PUBLIC);

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
    public GlobalSetting findByCode(Setting code) {
        return settingsRepository.findByCode(code)
                .orElseThrow(() -> new ContentNotFoundException("Настройка с кодом " + code + " не найдена"));
    }

    /**
     * Метод записывает глобальные настройки блога в таблицу global_settings,
     * если запрашивающий пользователь авторизован и является модератором.
     * <p>
     * Значения глобальных настроек
     * *
     * code                  name                               value
     * MULTIUSER_MODE        Многопользовательский режим        YES/NO
     * POST_PREMODERATION    Премодерация постов                YES/NO
     * STATISTICS_IS_PUBLIC  Показывать всем статистику блога   YES/NO
     *
     * @param response - запрос с фронта
     */
    public void saveSettings(SettingsResponse response) {
        updateSetting(Setting.MULTIUSER_MODE, response.isMultiUserMode());
        updateSetting(Setting.POST_PREMODERATION, response.isPostPreModeration());
        updateSetting(Setting.STATISTICS_IS_PUBLIC, response.isStatisticsIsPublic());
    }

    /**
     * Метод обновления значения настройки
     *
     * @param code  - код настройки
     * @param value - значение настройки
     * @throws ContentNotFoundException в случае, если настройка не найдена
     */
    private void updateSetting(Setting code, boolean value) {
        GlobalSetting setting = findByCode(code);
        setting.setValue(booleanToString(value));
        settingsRepository.save(setting);
    }

    /**
     * Метод преобразования boolean в String
     *
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
        return settingsRepository.existsByCodeAndValueIgnoreCase(Setting.STATISTICS_IS_PUBLIC, onValue);
    }
}
