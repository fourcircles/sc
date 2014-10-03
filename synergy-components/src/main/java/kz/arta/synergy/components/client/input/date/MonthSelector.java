package kz.arta.synergy.components.client.input.date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.menu.DropDownList;
import kz.arta.synergy.components.client.menu.FixedWidthList;
import kz.arta.synergy.components.client.menu.events.ListSelectionEvent;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.client.theme.ColorType;
import kz.arta.synergy.components.client.util.DateUtil;
import kz.arta.synergy.components.style.client.Constants;

import java.util.Date;

/**
 * User: user
 * Date: 01.08.14
 * Time: 14:47
 * Компонент выбора месяца в компоненте Календарь
 */
public class MonthSelector extends Composite {

    FlowPanel panel;

    /**
     * Назад на верхней панели
     */
    Image topBack = new Image(ImageResources.IMPL.navigationLeft());

    /**
     * Вперед на верхней панели
     */
    Image topForward = new Image(ImageResources.IMPL.navigationRight());

    /**
     * Надпись месяца
     */
    InlineLabel monthLabel = GWT.create(InlineLabel.class);

    /**
     * Надпись года
     */
    InlineLabel yearLabel = GWT.create(InlineLabel.class);

    /**
     * Список годов
     */
    FixedWidthList<Integer> yearsList;

    /**
     * Список месяцев  (0 - январь .. 11 - декабрь)
     */
    FixedWidthList<Integer> monthList;

    private DropDownList<Integer>.Item yearItem;

    private DropDownList<Integer>.Item monthItem;

    ArtaDatePicker picker;

    public MonthSelector(ArtaDatePicker datePicker) {
        picker = datePicker;
        init();
    }

    private void init() {
        panel = GWT.create(FlowPanel.class);
        initWidget(panel);
        setStyleName(SynergyComponents.resources.cssComponents().datePickerTop());
        if (picker.colorType == ColorType.BLACK) {
            addStyleName(SynergyComponents.resources.cssComponents().dark());
        }

        /*кнопка назад*/
        panel.add(topBack);
        topBack.getElement().getStyle().setFloat(LocaleInfo.getCurrentLocale().isRTL() ? Style.Float.RIGHT : Style.Float.LEFT);
        topBack.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        topBack.getElement().getStyle().setCursor(Style.Cursor.POINTER);

        /*инициализируем надпись месяца*/
        monthLabel.setText(DateUtil.getMonth(DateUtil.currentDate.getMonth()));
        monthLabel.setStyleName(SynergyComponents.resources.cssComponents().bigText());
        monthLabel.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            monthLabel.getElement().getStyle().setPaddingLeft(5, Style.Unit.PX);
        } else {
            monthLabel.getElement().getStyle().setPaddingRight(5, Style.Unit.PX);
        }
        EventBus monthBus = new SimpleEventBus();
        monthList = new FixedWidthList<Integer>(monthLabel, monthBus);
        monthList.setWidth(Constants.yearListWidth());
        monthList.setBorderTop(true);
        ListSelectionEvent.register(monthBus, new ListSelectionEvent.Handler<Integer>() {
            @Override
            public void onSelection(ListSelectionEvent<Integer> event) {
                monthItem = event.getItem();
                Date month = new Date(picker.currentDate.getTime());
                month.setMonth(event.getItem().getValue());
                picker.setCurrentDate(month, false);
                monthLabel.setText(DateUtil.getMonth(event.getItem().getValue()));
                monthList.hide();
            }
        });
        for (int i = 0; i < 12; i++) {
            DropDownList.Item item = monthList.addItem(DateUtil.getMonth(i), i);
            if ((Integer)item.getValue() == DateUtil.currentDate.getMonth()) {
                monthItem = item;
            }
        }

        monthLabel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                event.stopPropagation();
                removeStyleName(SynergyComponents.resources.cssComponents().pressed());
                if (monthList.isShowing()) {
                    monthList.hide();
                } else {
                    monthList.show(monthItem);
                }
            }
        });

        /*инициализируем надпись года*/
        yearLabel.setText((DateUtil.currentDate.getYear() + DateUtil.YEAR_OFFSET) + "");
        yearLabel.setStyleName(SynergyComponents.resources.cssComponents().bigText());
        yearLabel.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        EventBus bus = new SimpleEventBus();
        yearsList = new FixedWidthList<Integer>(yearLabel, bus);
        yearsList.setWidth(Constants.yearListWidth());
        yearsList.setBorderTop(true);
        ListSelectionEvent.register(bus, new ListSelectionEvent.Handler<Integer>() {
            @Override
            public void onSelection(ListSelectionEvent<Integer> event) {
                yearItem = event.getItem();
                yearLabel.setText(event.getItem().getValue() + "");
                yearsList.hide();
                Date year = new Date(picker.currentDate.getTime());
                year.setYear(event.getItem().getValue() - DateUtil.YEAR_OFFSET);
                picker.setCurrentDate(year, false);
            }
        });
        for (int i = DateUtil.currentDate.getYear() - 90; i < DateUtil.currentDate.getYear() + 10; i++) {
            DropDownList.Item item = yearsList.addItem((i + DateUtil.YEAR_OFFSET) + "", (i + DateUtil.YEAR_OFFSET));
            if ((Integer)item.getValue() == DateUtil.currentDate.getYear() + DateUtil.YEAR_OFFSET) {
                yearItem = item;
            }
        }

        yearLabel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                event.stopPropagation();
                removeStyleName(SynergyComponents.resources.cssComponents().pressed());
                if (yearsList.isShowing()) {
                    yearsList.hide();
                } else {
                    yearsList.show(yearItem);
                }
            }
        });

        FlowPanel labelPanel = GWT.create(FlowPanel.class);
        labelPanel.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        labelPanel.add(monthLabel);
        labelPanel.add(yearLabel);
        panel.add(labelPanel);

        /*кнопка вперед*/
        panel.add(topForward);
        topForward.getElement().getStyle().setFloat(LocaleInfo.getCurrentLocale().isRTL() ? Style.Float.LEFT : Style.Float.RIGHT);
        topForward.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        topForward.getElement().getStyle().setCursor(Style.Cursor.POINTER);
    }

}
