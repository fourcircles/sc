package kz.arta.synergy.components.client.table.column;

/**
 * User: user
 * Date: 18.11.14
 * Time: 16:52
 */
public abstract class AbstractEditableColumn<T> extends AbstractArtaColumn<T> {

    protected AbstractEditableColumn(String headerText) {
        super(headerText);
    }

    /**
     * Изменяет значение у объекта на заданное.
     * Возможно это можно как-то обобщить в будущем.
     * @param object объект
     * @param value значение
     */
    public abstract void setValue(T object, String value);
}
