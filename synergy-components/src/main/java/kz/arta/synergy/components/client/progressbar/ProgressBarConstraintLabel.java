package kz.arta.synergy.components.client.progressbar;

/**
 * User: vsl
 * Date: 24.11.14
 * Time: 11:28
 *
 * Надпись прогресс бара, которая применяется при значении в указанном интервале
 */
public class ProgressBarConstraintLabel implements ProgressBarCustomLabel {

    /**
     * Левая граница интервала
     */
    private double left;
    /**
     * Правая граница интервала
     */
    private double right;
    /**
     * Текст надписи
     */
    private String message;

    public ProgressBarConstraintLabel(double left, double right, String message) {
        if (left > right) {
            throw new IllegalArgumentException("левая граница должна быть меньше правой");
        }

        this.left = ProgressBar.constraint(left);
        this.right = ProgressBar.constraint(right);
        this.message = message;
    }

    @Override
    public boolean isApplicable(double value) {
        return value >= left && value <= right;
    }

    @Override
    public String getMessage(double value) {
        return message;
    }
}
