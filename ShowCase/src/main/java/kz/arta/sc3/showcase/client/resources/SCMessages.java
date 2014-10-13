package kz.arta.sc3.showcase.client.resources;

import kz.arta.i18n.shared.I18nFactory;
import org.xnap.commons.i18n.I18n;

/**
 * User: user
 * Date: 26.06.14
 * Time: 15:47
 */
public class SCMessages {
    private static final I18n I_18_N = I18nFactory.getI18n();

    public static final String SIZE = "Размер";

    private SCMessages() {
    }

    public static I18n i18n() {
        return I_18_N;
    }
}
