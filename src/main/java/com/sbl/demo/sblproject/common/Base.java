package com.sbl.demo.sblproject.common;

import com.sbl.demo.sblproject.domain.User;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Base {

    protected Logger log = LoggerFactory.getLogger(getClass());

    public User getUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }
}
