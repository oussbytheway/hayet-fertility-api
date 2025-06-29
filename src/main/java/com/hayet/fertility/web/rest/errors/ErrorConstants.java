package com.hayet.fertility.web.rest.errors;

import java.net.URI;

public final class ErrorConstants {

    public static final String ERR_CONCURRENCY_FAILURE = "error.concurrencyFailure";
    public static final String ERR_VALIDATION = "error.validation";
    public static final String PROBLEM_BASE_URL = "https://www.jhipster.tech/problem";
    public static final URI DEFAULT_TYPE = URI.create(PROBLEM_BASE_URL + "/problem-with-message");
    public static final URI CONSTRAINT_VIOLATION_TYPE = URI.create(PROBLEM_BASE_URL + "/constraint-violation");
    public static final URI INVALID_PASSWORD_TYPE = URI.create(PROBLEM_BASE_URL + "/invalid-password");
    public static final URI EMAIL_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/email-already-used");
    public static final URI LOGIN_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/login-already-used");
    public static final String LAST_NAME_IS_REQUIRED = "E01";
    public static final String AT_LEAST_ONE_NOTIFICATION_PREFERENCE_IS_REQUIRED = "E02";
    public static final String MOTIF_IS_REQUIRED = "E03";
    public static final String DUE_DATE_MUST_BE_IN_FUTURE = "E04";
    public static final String REMINDER_NEAR_EXECUTION_CANNOT_BE_MODIFIED = "E05";

    private ErrorConstants() {}
}
