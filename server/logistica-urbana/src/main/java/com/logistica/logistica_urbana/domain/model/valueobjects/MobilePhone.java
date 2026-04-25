package com.logistica.logistica_urbana.domain.model.valueobjects;

import java.util.regex.Pattern;

public record MobilePhone(String mobilePhone) {
    private static final Pattern DIGITS_ONLY = Pattern.compile("^\\+\\d{1,19}$");

    public MobilePhone {
        if (mobilePhone == null || mobilePhone.isBlank()) throw new RuntimeException("Mobile phone must not be empty");

        mobilePhone = mobilePhone.trim();

        if (!DIGITS_ONLY.matcher(mobilePhone).matches()) throw new RuntimeException("Mobile phone must contain digits only");

    }

}