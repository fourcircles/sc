package kz.arta.synergy.components.client.progressbar;

/**
 * User: vsl
 * Date: 24.11.14
 * Time: 11:27
 *
 * Надпись для прогресс-бара
 */
public interface ProgressBarCustomLabel {
    /**
     * @param value значение прогресс бара
     * @return текст надписи
     */
    String getMessage(double value);

    /**
     * Применима ли надпись к текущему значению
     *
     * @param value текущее значение
     * @return true - применима, false - неа
     */
    boolean isApplicable(double value);
}
