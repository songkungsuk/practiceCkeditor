package com.sbl.demo.sblproject.repository.web;

import com.github.pagehelper.Page;
import com.sbl.demo.sblproject.domain.User;
import com.sbl.demo.sblproject.domain.web.dto.UserDto;
import java.sql.SQLException;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {

    User selectByUsiId(String usiId);

    Page<UserDto> selectUserList(String srchOption, String srchWord) throws SQLException;

    void register(UserDto userDto) throws SQLException;

    UserDto selectByUsiNum(int usiNum);

    void updateUser(UserDto userDto) throws SQLException;

    void deleteUser(int usiNum) throws SQLException;


}
