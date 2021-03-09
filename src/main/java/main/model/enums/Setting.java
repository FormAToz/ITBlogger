package main.model.enums;

/**
 * Класс перечислений настроек приложения
 */
public enum Setting {
    MULTIUSER_MODE("Многопользовательский режим"),
    POST_PREMODERATION("Премодерация постов"),
    STATISTICS_IS_PUBLIC("Показывать всем статистику блога");

    private String name;

    Setting(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
