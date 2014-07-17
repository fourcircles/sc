package kz.arta.synergy.components.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import kz.arta.synergy.components.client.button.ImageButton;
import kz.arta.synergy.components.client.input.TextInput;
import kz.arta.synergy.components.client.menu.DropDownList;
import kz.arta.synergy.components.client.menu.MenuBase;
import kz.arta.synergy.components.client.resources.ImageResources;

/**
 * User: vsl
 * Date: 15.07.14
 * Time: 14:58
 * Комбо-бокс
 */
public class ComboBox extends Composite implements HasEnabled{

    /**
     * Основная панель
     */
    private FlowPanel panel;

    /**
     * Выпадающий список
     */
    private DropDownList list;

    /**
     * Выбранный элемент списка, который показывается в комбобоксе
     */
    private MenuBase.MenuItem shownItem;

    /**
     * Текст
     */
    private TextInput textLabel;

    /**
     * Кнопка раскрытия списка
     */
    private ImageButton dropDownButton;

    /**
     * Отключен или включен комбобокс
     */
    private boolean isEnabled;

    public ComboBox() {
        panel = new FlowPanel();
        initWidget(panel);

        isEnabled = true;

        list = new DropDownList(this) {
            @Override
            protected void itemSelected(MenuItem item) {
                super.itemSelected(item);
                showItem(item);
            }
        };
        list.setRelativeWidget(this);

        textLabel = new TextInput();
        textLabel.setReadOnly(true);
        textLabel.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);

        dropDownButton = new ImageButton(ImageResources.IMPL.comboBoxDropDown());
        dropDownButton.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);

        ClickHandler click = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (isEnabled) {
                    event.stopPropagation();
                    if (list.isShowing()) {
                        list.hide();
                    } else {
                        list.showUnderParent();
                    }
                }
            }
        };
        textLabel.addClickHandler(click);
        dropDownButton.addClickHandler(click);

        panel.add(textLabel);
        panel.add(dropDownButton);

        setStyleName(SynergyComponents.resources.cssComponents().comboBox());
        addStyleName(SynergyComponents.resources.cssComponents().mainText());
    }

    /**
     * Показать элемент списка в комбобоксе
     * @param item элемент списка
     */
    private void showItem(MenuBase.MenuItem item) {
        textLabel.setText(item.getText());
    }

    /**
     * Добавить элемент в список комбобокса
     * @param text текст элемента
     */
    public void addItem(String text) {
        list.addItem(text);
    }

    /**
     * Добавить элемент в список комбобокса.
     * @param text текст элемента
     * @param iconResource иконка элемента в списке
     */
    public void addItem(String text, ImageResource iconResource) {
        list.addItem(text, iconResource);
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
        if (!enabled) {
            addStyleName(SynergyComponents.resources.cssComponents().disabled());
        } else {
            removeStyleName(SynergyComponents.resources.cssComponents().disabled());
        }
    }
}

