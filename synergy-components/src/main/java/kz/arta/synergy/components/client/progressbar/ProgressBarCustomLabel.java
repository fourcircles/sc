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
     * Возвращает текст для прогресс-бара без опциональной полосы.
     *
     * @param value основное значение
     * @return текст соответствующий этому состоянию
     */
    String getMessage(double value);

    /**
     * Возвращает текст для прогресс-бара с опциональной полосой.
     * 
     * @param value основное значение
     * @param optional опцинальное значение
     * @return текст соответствующий этому состоянию
     */
    String getMessage(double value, double optional);
    
    /**
     * Применима ли надпись к текущему состоянию прогресс-бара
     *
     * @param value текущее значение
     * @return true - применима, false - неа
     */
    boolean isApplicable(double value);

    /**
     * {@link #isApplicable(double)}
     */
    boolean isApplicable(double value, double optionalValue);
}
