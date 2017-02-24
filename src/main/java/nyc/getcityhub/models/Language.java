package nyc.getcityhub.models;

import java.util.Arrays;

/**
 * Created by jackcook on 2/18/17.
 */
public enum Language {
    CHINESE_SIMPLIFIED("zh-Hans"),
    CHINESE_TRADITIONAL("zh-Hant"),
    ENGLISH("en-US"),
    FRENCH("fr-FR"),
    SPANISH("es-ES");

    private String id;

    Language(String id) {
        this.id = id;
    }

    public static boolean isLanguageSupported(String id) {
        for (Language language : Language.values()) {
            if (language.getId().equals(id)) {
                return true;
            }
        }

        return false;
    }

    public String getId() {
        return id;
    }
}
