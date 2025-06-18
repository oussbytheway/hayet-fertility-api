package com.hayet.fertility.domain.enumeration;

/**
 * The Language enumeration.
 */
public enum Language {
    EN,
    FR,
    AR;

    public String getValue() {
        return this.name().toLowerCase();
    }
}
