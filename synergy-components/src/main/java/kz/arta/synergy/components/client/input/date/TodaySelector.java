package kz.arta.synergy.components.client.input.date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.client.resources.Messages;

/**
 * User: user
 * Date: 01.08.14
 * Time: 17:12
 * Компонент выбора "Сегодня" в календаре
 */
public class TodaySelector extends Composite {

    FlowPanel panel;

    /**
     * Назад на верхней панели
     */
    Image back = new Image(ImageResources.IMPL.navigationLeft());

    /**
     * Вперед на верхней панели
     */
    Image forward = new Image(ImageResources.IMPL.navigationRight());

    /**
     * Надпись "Сегодня"
     */
    InlineLabel todayLabel = GWT.create(InlineLabel.class);

    public TodaySelector() {
        init();
    }

    private void init() {
        panel = GWT.create(FlowPanel.class);
        initWidget(panel);
        setStyleName(SynergyComponents.resources.cssComponents().datePickerTop());

        /*кнопка назад*/
        panel.add(back);
        back.getElement().getStyle().setFloat(LocaleInfo.getCurrentLocale().isRTL() ? Style.Float.RIGHT : Style.Float.LEFT);
        back.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        back.getElement().getStyle().setCursor(Style.Cursor.POINTER);

        /*инициализируем надпись месяца*/
        todayLabel.setText(Messages.i18n.tr("Сегодня"));
        todayLabel.setStyleName(SynergyComponents.resources.cssComponents().bigText());
        todayLabel.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        todayLabel.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);


        panel.add(todayLabel);

        /*кнопка вперед*/
        panel.add(forward);
        forward.getElement().getStyle().setFloat(LocaleInfo.getCurrentLocale().isRTL() ? Style.Float.LEFT : Style.Float.RIGHT);
        forward.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        forward.getElement().getStyle().setCursor(Style.Cursor.POINTER);
    }
}
