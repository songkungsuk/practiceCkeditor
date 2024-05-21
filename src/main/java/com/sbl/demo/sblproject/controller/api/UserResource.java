package com.sbl.demo.sblproject.controller.api;


import com.github.pagehelper.PageInfo;
import com.sbl.demo.sblproject.common.Base;
import com.sbl.demo.sblproject.common.NullHelper;
import com.sbl.demo.sblproject.domain.User;
import com.sbl.demo.sblproject.domain.web.PaginationVo;
import com.sbl.demo.sblproject.domain.web.dto.UserDto;
import com.sbl.demo.sblproject.service.MailService;
import com.sbl.demo.sblproject.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserResource extends Base {

    private final UserService userService;
    private final MailService mailService;

    // 권한 조회
    @GetMapping("/getAuth")
    public ResponseEntity<Boolean> auth(HttpSession session) {
        User user = getUser(session);
        if (NullHelper.isNull(user)) {
            return null;
        }
        return ResponseEntity.ok(user.getUsiAuth().equals("ROLE_ADMIN"));
    }

    // 회원 가입
    @PostMapping("/register")
    public ResponseEntity<Boolean> register(@RequestBody UserDto userDto) {
        boolean result = userService.register(userDto);
        return ResponseEntity.ok().body(result);
    }

    // 회원 조회
    @GetMapping("/info")
    public ResponseEntity<UserDto> selectUserByUsiNum(@Param("usiNum") int usiNum) {
        log.info("usiNum===>{}", usiNum);
        UserDto userDto = userService.selectUserByUsiNum(usiNum);
        return ResponseEntity.ok().body(userDto);
    }

    // 비밀번호 체크
    @GetMapping("/check-password")
    public ResponseEntity<Boolean> checkPassword(@Param("password") String password,
        HttpSession session) {
        log.info("password =====>{}", password);
        boolean result = userService.checkPassword(password, session);

        return ResponseEntity.ok().body(result);
    }

    //이메일 보내기
    @PostMapping("/email")
    public ResponseEntity<Boolean> sendEmail(HttpSession session) {

        boolean result = mailService.mailSend(session);

        return ResponseEntity.ok().body(result);
    }

    // 파일 업로드
    @PostMapping("/upload")
    public ResponseEntity<Boolean> fileUpload(@RequestBody MultipartFile file) throws Exception {
        log.info("file------->{}", file);

        boolean result = userService.fileUpload(file);

        return ResponseEntity.ok().body(result);
    }

}
