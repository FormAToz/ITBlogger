package main.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "captcha_codes")
public class CaptchaCode {
    /**
     * Класс кодов капчи
     *
     * id           id каптча
     * time         дата и время генерации кода капчи
     * code         код, отображаемый на картинке капчи
     * secretCode   код, передаваемый в параметре
     * */

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private LocalDateTime time;

    @NotNull
    @Column(columnDefinition = "TINYTEXT")
    private String code;

    @Column(name = "secret_code", columnDefinition = "TINYTEXT")
    private String secretCode;

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSecretCode() {
        return secretCode;
    }

    public void setSecretCode(String secretCode) {
        this.secretCode = secretCode;
    }
}
