package ru.up01.app.ui;

import java.util.regex.Pattern;

public final class Validators {
    private Validators() {}

    // Разрешаем: +, цифры, пробелы, скобки, дефис. Длина 5..20
    private static final Pattern PHONE = Pattern.compile("^[+0-9()\\-\\s]{5,20}$");

    public static String requireNotBlank(String value, String fieldName, int maxLen) {
        if (value == null || value.trim().isEmpty()) {
            return "Поле \"" + fieldName + "\" обязательно.";
        }
        if (maxLen > 0 && value.trim().length() > maxLen) {
            return "Поле \"" + fieldName + "\" слишком длинное (макс. " + maxLen + ").";
        }
        return null;
    }

    public static String validatePhone(String value, String fieldName) {
        if (value == null) return null;
        String v = value.trim();
        if (v.isEmpty()) return null; // если телефон необязательный — пустое значение допустимо
        if (!PHONE.matcher(v).matches()) {
            return "Поле \"" + fieldName + "\" имеет неверный формат телефона.";
        }
        return null;
    }
}
