package main.model;

import main.model.enums.Setting;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "global_settings")
public class GlobalSetting {
    /**
     * Класс глобальных настроек движка
     *
     * id     id настройки
     * code   системное имя настройки
     * name   название настройки
     * value  значение настройки
     *
     * Значения глобальных настроек
     *
     * code                  name                               value
     * MULTIUSER_MODE        Многопользовательский режим        YES/NO
     * POST_PREMODERATION    Премодерация постов                YES/NO
     * STATISTICS_IS_PUBLIC  Показывать всем статистику блога   YES/NO
     * */

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @Column(columnDefinition = "VARCHAR(32)")
    @Enumerated(EnumType.STRING)
    private Setting code;

    @NotNull
    private String name, value;

    public GlobalSetting() {
    }

    public GlobalSetting(@NotNull Setting code, @NotNull String name, @NotNull String value) {
        this.code = code;
        this.name = name;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Setting getCode() {
        return code;
    }

    public void setCode(Setting code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
