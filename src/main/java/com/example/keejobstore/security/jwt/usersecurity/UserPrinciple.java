package com.example.keejobstore.security.jwt.usersecurity;

import com.example.keejobstore.entity.Role;
import com.example.keejobstore.entity.User;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserPrinciple implements UserDetails {
    private Integer id;
    private String username;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private Role role;
    public UserPrinciple(Integer id, String username, String email,
                         String password, Collection<? extends GrantedAuthority> authorities, Role role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.role = role;
    }

    public static UserPrinciple build(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        // Ajoutez le rôle de l'utilisateur aux autorisations
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().toString()));

        return new UserPrinciple(user.getId(), user.getUsername(),
                user.getEmail(), user.getPassword(), authorities, user.getRole());
    }

   /* public static UserPrinciple build(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        return new UserPrinciple(user.getId(), user.getUsername(),
                user.getEmail(), user.getPassword(), authorities);
    } */

    public Integer getId() {
        return id;
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
        return this.username;
    }

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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
