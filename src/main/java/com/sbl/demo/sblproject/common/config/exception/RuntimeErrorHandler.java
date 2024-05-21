package com.sbl.demo.sblproject.common.config.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RuntimeErrorHandler {

    @ExceptionHandler(BizException.class)
    public String codeError(Exception e, Model m) {
        m.addAttribute("exception", e);
        return "error";
    }

}
