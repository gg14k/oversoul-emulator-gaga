package org.gaga.oversoul.service.login;

public enum LoginStatus {
    SUCCESS,
    INVALID_CREDENTIALS,
    PROFILE_NOT_FOUND,
    NO_CHARACTERS,
    ACTIVE_CHARACTER_NOT_FOUND,
    ALREADY_AUTHENTICATED,
    INTERNAL_ERROR
}