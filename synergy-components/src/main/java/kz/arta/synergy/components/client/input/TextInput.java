package kz.arta.synergy.components.client.input;

/**
 * User: user
 * Date: 15.07.14
 * Time: 11:08
 * Компонент ввода однострочного текста
 */
public class TextInput extends CommonInput {

    /**
     * Максимальное количество символов
     */
    private int maxWidth = -1;

    /**
     * Минимальное количество символов
     */
    private int minWidth = -1;

    public TextInput() {
        super();
    }

    public TextInput(boolean allowEmpty) {
        super(allowEmpty);
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public int getMinWidth() {
        return minWidth;
    }

    public void setMinWidth(int minWidth) {
        this.minWidth = minWidth;
    }

    //todo check maxWidth and minWidth
}
