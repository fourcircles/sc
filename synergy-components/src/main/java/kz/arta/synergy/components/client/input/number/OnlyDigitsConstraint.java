package kz.arta.synergy.components.client.input.number;

/**
 * User: vsl
 * Date: 16.10.14
 * Time: 11:07
 *
 * Ограничение: можно вводить только цифры
 */
public class OnlyDigitsConstraint implements InputConstraint {
    private static final OnlyDigitsConstraint SINGLETON = new OnlyDigitsConstraint();

    protected OnlyDigitsConstraint() {
    }

    public static OnlyDigitsConstraint getInstance() {
        return SINGLETON;
    }

    @Override
    public boolean allowChange(String newText) {
        for (Character ch : newText.toCharArray()) {
            if (!Character.isDigit(ch)) {
                return false;
            }
        }

        return true;
    }
}
