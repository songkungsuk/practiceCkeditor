package com.sbl.demo.sblproject.controller.api;

import com.github.pagehelper.PageInfo;
import com.sbl.demo.sblproject.common.Base;
import com.sbl.demo.sblproject.domain.web.PaginationVo;
import com.sbl.demo.sblproject.domain.web.dto.UserDto;
import com.sbl.demo.sblproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v3/admin")
@RequiredArgsConstructor
public class AdminResource extends Base {

    private final UserService userService;

    // 유저 전체 조회
    @PostMapping("/list")
    public ResponseEntity<PageInfo<UserDto>> selectUserList(@RequestBody PaginationVo vo) {
        log.debug("REST 요청 유저 전체 조회");
        PageInfo<UserDto> list = userService.selectUserList(vo).toPageInfo();
        return ResponseEntity.ok().body(list);
    }

    // 회원 수정
    @PatchMapping("/update")
    public ResponseEntity<Boolean> updateUser(@RequestBody UserDto userDto) {
        log.info("userDto============>><><>>>>>>>>>{}", userDto);
        boolean result = userService.updateUser(userDto);
        return ResponseEntity.ok().body(result);
    }

    // 회원 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> deleteUser(@Param("usiNum") int usiNum) {
        boolean result = userService.deleteUser(usiNum);
        return ResponseEntity.ok().body(result);
    }
}
