package com.sbl.demo.sblproject.controller.page;

import com.sbl.demo.sblproject.common.Base;
import com.sbl.demo.sblproject.common.NullHelper;
import com.sbl.demo.sblproject.domain.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class AdminController extends Base {

    @GetMapping("/management")
    public String userManagement(HttpSession session) {
        User user = getUser(session);
        if (NullHelper.isNull(user)) {
            return "/login";
        }
        return "/admin/userManagement";
    }

    @GetMapping("/description")
    public String description(HttpSession session) {
        User user = getUser(session);
        if (NullHelper.isNull(user)) {
            return "/login";
        }
        return "/admin/description";
    }

    @GetMapping("/user-detail/{usiNum}")
    public String userDetail(HttpSession session, @PathVariable(value = "usiNum") int usiNum){
        User user = getUser(session);
        if (NullHelper.isNull(user)) {
            return "/login";
        }
        return "/admin/userDetail";
    }

}
