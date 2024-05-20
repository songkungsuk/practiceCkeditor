package com.sbl.demo.sblproject.common.config.security.direct;

import com.sbl.demo.sblproject.domain.User;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;

public class PrincipalDetails implements UserDetails, Serializable {

    private static final long serialVersionUID = 1L;

    @Getter
    private User user;

    public PrincipalDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collections = new HashSet<>();
        collections.add(() -> {
            return user.getUsiAuth();
        });

        return collections;
    }

    @Override
    public String getPassword() {
        return user.getUsiPwd();
    }

    @Override
    public String getUsername() {
        return user.getUsiId();
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
}
