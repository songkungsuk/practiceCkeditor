package com.sbl.demo.sblproject.domain.web.enums;

public enum TokenGrantType {
    Bearer("Bearer"),
    Basic("Basic");

    private final String name;

    TokenGrantType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
