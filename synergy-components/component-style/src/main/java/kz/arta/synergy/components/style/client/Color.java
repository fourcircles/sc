package kz.arta.synergy.components.style.client;

/**
 * User: vsl
 * Date: 04.08.14
 * Time: 14:34
 *
 * Цвет.
 * Предоставляет возможность вернуть цвет с полной прозрачностью в rgba формате и
 * в формате ie9.
 */
public class Color {
    private String hex;

    public Color(String hex) {
        this.hex = hex;
    }

    /**
     * Возвращает цвет в rgba формате с полной прозрачностью
     */
    public String alpha0() {
        return ColorUtils.toRgba(hex, "0");
    }

    /**
     * Возвращает цвет с полной прозрачностью в формате напоминающем hex, который придумали в IE9.
     * Например, #00abcdef - первый две цифры отвечают за прозрачность
     */
    public String alpha0IE() {
        return "#00" + hex.substring(1);
    }

    public String hex() {
        return hex;
    }
}
