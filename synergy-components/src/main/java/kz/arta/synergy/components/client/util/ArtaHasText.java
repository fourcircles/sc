package kz.arta.synergy.components.client.util;

import com.google.gwt.user.client.ui.HasText;

/**
 * User: vsl
 * Date: 25.07.14
 * Time: 11:24
 *
 * Интерфейс расширяет HasText, добавляя метод для стиля шрифта.
 * Предполагается, что все элементы с текстом должны знать о стиле шрифта
 * для определения длины.
 */
public interface ArtaHasText extends HasText {
    /**
     * Стиль шрифта
     */
    String getFontStyle();
}
