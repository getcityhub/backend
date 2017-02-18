package nyc.getcityhub.models;

import java.util.Arrays;

/**
 * Created by jackcook on 2/18/17.
 */
public enum Language {
    CHINESE_SIMPLIFIED("zh-Hans"),
    CHINESE_TRADITIONAL("zh-TW"),
    ENGLISH("en-US"),
    FRENCH("fr-FR"),
    SPANISH("es-ES");

    private String id;

    Language(String id) {
        this.id = id;
    }

    public static boolean isLanguageSupported(String string) {
        return Arrays.asList(Language.values()).contains(string);
    }

    public String getId() {
        return id;
    }
}
