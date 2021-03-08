package main.security;

import main.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Класс пользователя, у которого Spring получает Authorities
 */
public class SecurityUser implements UserDetails {

    private final String userName;
    private final String password;
    private final List<SimpleGrantedAuthority> authorities;

    public SecurityUser(String userName, String password, List<SimpleGrantedAuthority> authorities) {
        this.userName = userName;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserDetails getFromUser(User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                true,true,true,true,
                user.getRole().getAuthorities());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    // Далее поля не учитываются, т. к. в бд у нас отсутсвует данная информация
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
