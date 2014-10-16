package kz.arta.synergy.components.client.input.number;

import com.google.gwt.i18n.client.LocaleInfo;

/**
 * User: vsl
 * Date: 16.10.14
 * Time: 11:05
 *
 * Ограничение: можно вводить цифры и один разделитель (зависит от локали)
 */
public class DoubleConstraint implements InputConstraint {
    private static final DoubleConstraint SINGLETON = new DoubleConstraint();

    protected DoubleConstraint() {
    }

    public static DoubleConstraint getInstance() {
        return SINGLETON;
    }

    @Override
    public boolean allowChange(String newText) {
        int separatorCount = 0;
        String separator = LocaleInfo.getCurrentLocale().getNumberConstants().decimalSeparator();

        int i = 0;
        while (i < newText.length()) {
            if (separator.equals(newText.substring(i, i + separator.length()))) {
                i += separator.length();
                separatorCount++;
            } else {
                i++;
            }
        }

        if (separatorCount > 1) {
            return false;
        }

        String noSeparator = newText.replace(separator, "");

        for (Character ch : noSeparator.toCharArray()) {
            if (!Character.isDigit(ch)) {
                return false;
            }
        }

        return true;
    }
}
