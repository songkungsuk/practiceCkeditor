package com.sbl.demo.sblproject.common.config.security.direct;

import com.sbl.demo.sblproject.domain.User;
import com.sbl.demo.sblproject.repository.web.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usiId) throws UsernameNotFoundException {
        final User load = userRepository.selectByUsiId(usiId);
        if (load == null) {
            throw new UsernameNotFoundException("존재하지 않는 계정입니다. 관리자에게 문의하세요.");
        }
        return new PrincipalDetails(load);
    }
}
