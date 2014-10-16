package kz.arta.synergy.components.client.input.number;

import com.google.gwt.i18n.client.NumberFormat;

/**
 * User: vsl
 * Date: 16.10.14
 * Time: 11:33
 *
 * Ограничение ввода на максимальное значение
 */
public class MaxNumberConstraint implements InputConstraint{
    private int max;

    public MaxNumberConstraint(int max) {
        this.max = max;
    }

    @Override
    public boolean allowChange(String newText) {
        if (newText.isEmpty()) {
            return true;
        }

        try {
            double number = NumberFormat.getDecimalFormat().parse(newText);
            return number < max;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
