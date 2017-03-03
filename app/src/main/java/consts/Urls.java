package consts;

/**
 * Created by Paul on 3/2/17.
 */

public class Urls {

    public static enum RequestType {
        TRANSLATE,
        LANGS
    }

    public static final String GOOGLE_TRANSLATE = "https://www.googleapis.com/language/translate/v2";
    public static final String GOOGLE_LANGS = "https://translation.googleapis.com/language/translate/v2/languages?key=" + Consts.GOOGLE_TRANSLATE_API_KEY;
}
