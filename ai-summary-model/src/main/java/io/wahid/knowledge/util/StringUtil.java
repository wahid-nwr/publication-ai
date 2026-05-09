package io.wahid.knowledge.util;

import java.text.Normalizer;

public class StringUtil {
    private StringUtil() {}
    public static String normalize(String s) {
        return s == null ? "" : Normalizer.normalize(s.trim(), Normalizer.Form.NFC);
    }
}
