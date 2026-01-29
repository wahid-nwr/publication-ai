package io.wahid.publication.ai.util;

import java.text.Normalizer;

public class PublicationStringUtil {
    private PublicationStringUtil() {}
    public static String normalize(String s) {
        return s == null ? "" : Normalizer.normalize(s.trim(), Normalizer.Form.NFC);
    }
}
