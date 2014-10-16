package kz.arta.synergy.components.client.input.number;

/**
 * User: vsl
 * Date: 16.10.14
 * Time: 10:31
 *
 * Ограничение ввода
 */
public interface InputConstraint {
    boolean allowChange(String newText);
}
