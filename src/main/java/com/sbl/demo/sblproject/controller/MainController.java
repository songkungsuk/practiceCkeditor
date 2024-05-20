package com.sbl.demo.sblproject.controller;

import com.sbl.demo.sblproject.common.Base;
import com.sbl.demo.sblproject.domain.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MainController extends Base {

    @GetMapping("/main")
    public String main(HttpSession session) {
        User user = getUser(session);
        if (user == null) {
            return "/login";
        }
        return "/main";
    }

    @GetMapping(value = {"/", "/login"})
    public String index() {
        return "/login";
    }

    @GetMapping("/logout")
    public boolean logout(HttpSession session) {
        session.removeAttribute("user");
        return true;
    }

    @GetMapping("/register")
    public String register() {
        return "/user/register";
    }
}
