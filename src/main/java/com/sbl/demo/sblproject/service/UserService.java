package com.sbl.demo.sblproject.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sbl.demo.sblproject.common.Base;
import com.sbl.demo.sblproject.common.NullHelper;
import com.sbl.demo.sblproject.domain.User;
import com.sbl.demo.sblproject.domain.web.PaginationVo;
import com.sbl.demo.sblproject.domain.web.dto.UserDto;
import com.sbl.demo.sblproject.repository.web.UserRepository;
import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService extends Base {

    private final UserRepository userRepository;
    final String ROLE_USER = "ROLE_USER";

    public Page<UserDto> selectUserList(PaginationVo vo) {
        try {
            PageHelper.startPage(vo.getPageNum(), vo.getPageSize());
            Page<UserDto> list = userRepository.selectUserList(vo.getSrchOption(),
                vo.getSrchWord());
            log.info("list---->{}", list);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public boolean register(UserDto userDto) {
        try {
            userDto.setUsiAuth(ROLE_USER);
            userRepository.register(userDto);
            return true;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public UserDto selectUserByUsiNum(int usiNum) {
        UserDto user = userRepository.selectByUsiNum(usiNum);
        return user;
    }

    public boolean checkPassword(String password, HttpSession session) {
        User tmpUser = getUser(session);
        User user = userRepository.selectByUsiId(tmpUser.getUsiId());
        password = "{noop}" + password;
        boolean result = password.equals(user.getUsiPwd());
        return result;
    }

    public boolean updateUser(UserDto userDto) {
        try {
            if (NullHelper.isEmpty(userDto.getPassword())) {
                userDto.setUsiPwd(userDto.getPassword());
            }
            userRepository.updateUser(userDto);

            return true;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public boolean deleteUser(int usiNum) {
        try {
            userRepository.deleteUser(usiNum);
            return true;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
