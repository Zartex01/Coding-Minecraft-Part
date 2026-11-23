package com.zartex.tycoon.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.Locale;

public final class Text {

    private Text() {
    }

    public static String color(String s) {
        return s.replace('&', '\u00a7');
    }

    public static Component component(String s) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(s);
    }

    public static String money(long n) {
        return String.format(Locale.US, "%,d", n);
    }
}
