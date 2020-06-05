package main.model.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "Captcha_codes")
public class CaptchaCode {
    /**
     * Класс кодов капч
     *
     * id           id каптча
     * time         дата и время генерации кода капчи
     * code         код, отображаемый на картинкке капчи
     * secretCode   код, передаваемый в параметре
     * */

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;

    @NotNull
    @Column(columnDefinition = "TINYTEXT")
    private String code;

    @Column(name = "secret_code", columnDefinition = "TINYTEXT")
    private String secretCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
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
