package kz.arta.synergy.components.client.table;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.button.SimpleButton;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.client.resources.Messages;

/**
 * User: vsl
 * Date: 17.09.14
 * Time: 14:43
 *
 * Шапка для таблицы
 */
public class TableHat extends Composite {
    /**
     * Корневой элемент
     */
    private FlowPanel root;

    /**
     * Лейбл для названия таблицы
     */
    private InlineLabel nameLabel;

    /**
     * Кнопка "добавить"
     */
    private SimpleButton addButton;

    /**
     * Включен ли пагинатор
     */
    private boolean pagerEnabled;

    /**
     * Всегда ли показывать пагинатор.
     * Имеет приоритет над pagerEnabled.
     * При изменении на false проявляется значение pagerEnabled.
     * Используется для случая когда в таблице несколько страниц и надо всегда показывать пагинатор
     */
    private boolean pagerEnabledAlways;

    /**
     * Пагинатор. Для таблицы всегда с текстом.
     */
    private Pager pager;

    /**
     * Таблица
     */
    private Table table;

    public TableHat(Table table) {
        root = new FlowPanel();
        initWidget(root);
        root.setStyleName(SynergyComponents.resources.cssComponents().hat());

        this.table = table;

        nameLabel = new InlineLabel("");
        nameLabel.setStyleName(SynergyComponents.resources.cssComponents().bigText());
        root.add(nameLabel);
    }

    /**
     * @return название таблицы
     */
    public String getName() {
        return nameLabel.getText();
    }

    /**
     * Изменить название таблицы
     * @param name название
     */
    public void setName(String name) {
        if (name == null) {
            name = "";
        }
        nameLabel.setText(name);

        nameLabel.setText(name);
    }

    /**
     * @return показывать ли кнопку
     */
    public boolean hasButton() {
        return addButton.isAttached();
    }

    /**
     * Показать или скрыть кнопку.
     */
    public void enableButton(boolean enabled) {
        if (addButton == null) {
            addButton = new SimpleButton(Messages.i18n.tr("Добавить"), ImageResources.IMPL.zoom());
        }
        if (enabled) {
            if (!addButton.isAttached()) {
                root.insert(addButton, 1);
            }
        } else {
            addButton.removeFromParent();
        }
    }

    /**
     * Добавляет хендлер на клик кнопки "добавить"
     */
    public HandlerRegistration addAddButtonHandler(ClickHandler handler) {
        return addButton.addClickHandler(handler);
    }

    public Pager getPager() {
        return pager;
    }

    /**
     * Показать/скрыть пагинатор
     */
    private void innerEnablePager(boolean enabled) {
        if (enabled) {
            if (pager == null) {
                pager = new Pager(true);
                pager.setDisplay(table);
            }
            if (!pager.isAttached()) {
                root.add(pager);
            }
        } else {
            if (pager != null) {
                pager.removeFromParent();
            }
        }
        pagerEnabled = enabled;
    }

    /**
     * Учитывает взаимодействие pagerEnabled и pagerEnabledAlways
     */
    private void updatePagerVisibility() {
        boolean show;
        if (pagerEnabledAlways) {
            show = true;
        } else {
            show = pagerEnabled;
        }

        innerEnablePager(show);
    }

    public void enablePager(boolean enabled) {
        pagerEnabled = enabled;
        updatePagerVisibility();
    }

    public void enablePagerAlways(boolean always) {
        pagerEnabledAlways = always;
        updatePagerVisibility();
    }
}
