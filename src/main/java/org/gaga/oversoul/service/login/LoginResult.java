package org.gaga.oversoul.service.login;

public record LoginResult(
        LoginStatus status
) {

    public boolean isSuccess() {
        return status == LoginStatus.SUCCESS;
    }

    public static LoginResult success() {
        return new LoginResult(
                LoginStatus.SUCCESS
        );
    }

    public static LoginResult failure(
            LoginStatus status
    ) {
        return new LoginResult(status);
    }
}