package yogi.com.translateapp.consts;

/**
 * Created by Paul on 3/2/17.
 */

public class Urls {

    public static enum RequestType {
        TRANSLATE,
        TRANSLATE_OCR,
        TRANSLATE_VOICE,
        LANGS
    }

    public static final String GOOGLE_TRANSLATE = "https://www.googleapis.com/language/translate/v2";
    public static final String GOOGLE_LANGS = "https://translation.googleapis.com/language/translate/v2/languages?key=" + Consts.GOOGLE_TRANSLATE_API_KEY;

    public static final String OCR_API = "https://api.ocr.space/parse/image";
    public static final String OCR_API_KEY = "e5d4b9123688957";

}
