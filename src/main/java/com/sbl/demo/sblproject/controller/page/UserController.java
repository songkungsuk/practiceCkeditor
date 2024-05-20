package com.sbl.demo.sblproject.controller.page;

import com.sbl.demo.sblproject.common.Base;
import com.sbl.demo.sblproject.common.NullHelper;
import com.sbl.demo.sblproject.domain.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class UserController extends Base {

    @GetMapping("/dashBoard")
    public String dashBoard(HttpSession session) {
        User user = getUser(session);
        if (NullHelper.isNull(user)) {
            return "/login";
        }
        return "/user/dashBoard";
    }

    @GetMapping("/report")
    public String report(HttpSession session) {
        User user = getUser(session);
        if (NullHelper.isNull(user)) {
            return "/login";
        }
        return "/user/report";
    }



}
