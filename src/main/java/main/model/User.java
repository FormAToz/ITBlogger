package main.model;

import main.model.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Класс Пользователи
 *
 * MODERATOR_VALUE целочисленное значение для проверки, является ли пользователь модератором
 * id            id пользователя
 * isModerator   является ли пользователь модератором (может ли править глобальные настройки сайта и модерировать посты)
 * regTime       дата и время регистрации пользователя
 * name          имя пользователя
 * email         e-mail пользователя
 * password      хэш пароля пользователя
 * code          код для восстановления пароля, может быть NULL
 * photo         фотография (ссылка на файл), может быть NULL
 * */
@Component
@Entity
@Table(name = "users")
public class User {

    @Transient
    private static int MODERATOR_VALUE;

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Column(name = "is_moderator", columnDefinition = "TINYINT")
    private int isModerator;

    @NotNull
    @Column(name = "reg_time")
    private LocalDateTime regTime;

    @NotNull
    private String name, email, password;

    private String code;

    @Column(columnDefinition = "TEXT")
    private String photo;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Post> posts;

    @OneToMany(mappedBy = "userId", fetch = FetchType.LAZY)
    private List<PostComment> comments;

    /**
     * Метод инициализации значения MODERATOR_VALUE из файла настроек
     */
    @Autowired
    private void setModeratorValue(@Value("${user.moderator-value}") int value) {
        MODERATOR_VALUE = value;
    }

    /**
     * Метод получения роли пользователя
     */
    public Role getRole() {
        return isModerator == MODERATOR_VALUE ? Role.MODERATOR : Role.USER;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsModerator() {
        return isModerator;
    }

    public void setIsModerator(int isModerator) {
        this.isModerator = isModerator;
    }

    public LocalDateTime getRegTime() {
        return regTime;
    }

    public void setRegTime(LocalDateTime regTime) {
        this.regTime = regTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public List<PostComment> getComments() {
        return comments;
    }

    public void setComments(List<PostComment> comments) {
        this.comments = comments;
    }
}
