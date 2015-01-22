package kz.arta.sc3.showcase.client.resources;

import kz.arta.i18n.shared.I18nFactory;
import org.xnap.commons.i18n.I18n;

/**
 * User: user
 * Date: 26.06.14
 * Time: 15:47
 */
public class Messages {
    public static final String SIZE = "Размер";

    private Messages() {
    }

    public static I18n i18n() {
        return I18nFactory.getI18n();
    }
}
