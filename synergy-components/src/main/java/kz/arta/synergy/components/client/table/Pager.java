package kz.arta.synergy.components.client.table;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.cellview.client.AbstractPager;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.view.client.Range;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.button.ImageButton;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.client.resources.Messages;
import kz.arta.synergy.components.style.client.Constants;

/**
 * User: vsl
 * Date: 17.09.14
 * Time: 10:06
 *
 * Пагинатор
 */
public class Pager extends AbstractPager implements HasEnabled {
    /**
     * корневая панель
     */
    private FlowPanel root;

    /**
     * Кнопка назад
     */
    private ImageButton previousButton;

    /**
     * Кнопка вперед
     */
    private ImageButton nextButton;

    /**
     * Текст
     */
    private InlineLabel label;

    /**
     * Отображает ли текст
     */
    private boolean hasText;

    /**
     * Включен ли пагинатор
     */
    private boolean enabled = true;

    /**
     * Статус кнопки "назад", если бы пагинатор был включен.
     */
    private boolean previousEnabled;

    /**
     * Статус кнопки "вперед", если бы пагинатор был включен.
     */
    private boolean nextEnabled;

    /**
     * @param hasText true - дополнительный текст отображается, false - просто две кнопки
     */
    public Pager(boolean hasText) {
        root = new FlowPanel();
        initWidget(root);
        root.setStyleName(SynergyComponents.resources.cssComponents().pager());

        this.hasText = hasText;

        previousButton = new ImageButton(ImageResources.IMPL.pagerLeft());
        nextButton = new ImageButton(ImageResources.IMPL.pagerRight());

        root.add(previousButton);
        if (hasText) {
            label = createLabel();
            root.add(label);
        }

        updateWidth();
        root.add(nextButton);

        previousButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.getNativeButton() != NativeEvent.BUTTON_LEFT) {
                    return;
                }
                previousPage();
            }
        });
        nextButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.getNativeButton() != NativeEvent.BUTTON_LEFT) {
                    return;
                }
                nextPage();
            }
        });
    }

    public Pager() {
        this(false);
    }

    /**
     * Создает виджет для текста
     */
    private InlineLabel createLabel() {
        InlineLabel label = new InlineLabel();
        label.setStyleName(SynergyComponents.resources.cssComponents().mainText());
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            //-1 из-за наложения границы
            label.getElement().getStyle().setRight(Constants.PAGER_BUTTON_WIDTH - 1, Style.Unit.PX);
        } else {
            label.getElement().getStyle().setLeft(Constants.PAGER_BUTTON_WIDTH - 1, Style.Unit.PX);
        }
        //на данный момент не имеет смысла разворачивать фразу для ar локали
        label.getElement().setAttribute("dir", "ltr");
        return label;
    }

    public boolean hasText() {
        return hasText;
    }

    /**
     * Добавляет или удаляет текст
     */
    public void setHasText(boolean hasText) {
        this.hasText = hasText;
        if (hasText) {
            if (label == null) {
                label = createLabel();
            }
            updateText();
            root.insert(label, 1);
        } else {
            root.remove(label);
        }
        updateWidth();
    }

    /**
     * Обновляет ширину в зависимости от наличия текста
     */
    private void updateWidth() {
        int width = Constants.PAGER_BUTTON_WIDTH * 2 - 1;
        if (hasText) {
            width += Constants.PAGER_TEXT_WIDTH + Constants.PAGER_TEXT_PADDING * 2;
        }
        root.getElement().getStyle().setWidth(width, Style.Unit.PX);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        updateTextPosition();
        updateButtons();
    }

    /**
     * Изменяет позицию текста, для сохранение центрального положения текста по вертикали.
     */
    private void updateTextPosition() {
        if (isAttached() && label != null) {
            int top = (Constants.PAGER_HEIGHT - Constants.BORDER_WIDTH * 2 - label.getOffsetHeight()) / 2;
            label.getElement().getStyle().setTop(Math.max(0, top), Style.Unit.PX);
        }
    }

    /**
     * Изменяет текст в зависимости от выбранной страницы
     */
    private void updateText() {
        if (label == null) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        Range range = getDisplay().getVisibleRange();
        builder.append(range.getStart());
        builder.append('-');
        builder.append(range.getStart() + range.getLength());
        builder.append(" ").append(Messages.i18n().tr("из")).append(" ");
        if (getDisplay().isRowCountExact()) {
            builder.append(getDisplay().getRowCount());
        } else {
            builder.append(Messages.i18n().tr("множества"));
        }
        label.setText(builder.toString());
        updateTextPosition();
    }

    /**
     * Отключает/включает кнопки в зависимости от того выбраны ли
     * первая или последняя страница
     */
    private void updateButtons() {
        if (hasNextPage()) {
            nextEnabled = true;
        } else {
            nextEnabled = false;
        }
        if (hasPreviousPage()) {
            previousEnabled = true;
        } else {
            previousEnabled = false;
        }

        if (isEnabled()) {
            nextButton.setEnabled(nextEnabled);
            previousButton.setEnabled(previousEnabled);
        }
    }

    @Override
    protected void onRangeOrRowCountChanged() {
        updateText();
        updateButtons();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            if (enabled) {
                nextButton.setEnabled(nextEnabled);
                previousButton.setEnabled(previousEnabled);
            } else {
                nextButton.setEnabled(false);
                previousButton.setEnabled(false);
            }
            this.enabled = enabled;
        }
    }
}
