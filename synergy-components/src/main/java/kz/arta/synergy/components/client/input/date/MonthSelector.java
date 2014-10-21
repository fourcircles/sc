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
import kz.arta.synergy.components.client.menu.MenuItem;
import kz.arta.synergy.components.client.menu.events.MenuItemSelection;
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
    DropDownList<Integer> yearsList;

    /**
     * Список месяцев  (0 - январь .. 11 - декабрь)
     */
    DropDownList<Integer> monthList;

    ArtaDatePicker picker;

    public MonthSelector(ArtaDatePicker datePicker) {
        picker = datePicker;
        init();
    }

    private void init() {
        panel = GWT.create(FlowPanel.class);
        initWidget(panel);
        setStyleName(SynergyComponents.getResources().cssComponents().datePickerTop());
        if (picker.colorType == ColorType.BLACK) {
            addStyleName(SynergyComponents.getResources().cssComponents().dark());
        }

        /*кнопка назад*/
        panel.add(topBack);
        topBack.getElement().getStyle().setFloat(LocaleInfo.getCurrentLocale().isRTL() ? Style.Float.RIGHT : Style.Float.LEFT);
        topBack.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        topBack.getElement().getStyle().setCursor(Style.Cursor.POINTER);

        /*инициализируем надпись месяца*/
        monthLabel.setText(DateUtil.getMonth(DateUtil.getCurrentDate().getMonth()));
        monthLabel.setStyleName(SynergyComponents.getResources().cssComponents().bigText());
        monthLabel.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            monthLabel.getElement().getStyle().setPaddingLeft(5, Style.Unit.PX);
        } else {
            monthLabel.getElement().getStyle().setPaddingRight(5, Style.Unit.PX);
        }

        EventBus monthBus = new SimpleEventBus();

        monthList = new DropDownList<Integer>();
        monthList.setMinWidth(Constants.YEAR_LIST_WIDTH);
        monthList.addAutoHidePartner(monthLabel.getElement());

        monthList.addDaggerItemSelectionHandler(new MenuItemSelection.Handler<Integer>() {
            @Override
            public void onItemSelection(MenuItemSelection<Integer> event) {
                Date month = new Date(picker.currentDate.getTime());
                month.setMonth(event.getItem().getUserValue());
                picker.setCurrentDate(month, false);
                monthLabel.setText(DateUtil.getMonth(event.getItem().getUserValue()));
                monthList.hide();
            }
        });
        for (int i = 0; i < 12; i++) {
            monthList.addItem(new MenuItem<Integer>(i, DateUtil.getMonth(i)));
        }
        monthList.selectItem(monthList.get(DateUtil.getCurrentDate().getMonth()), true, false);

        monthLabel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                event.stopPropagation();
                removeStyleName(SynergyComponents.getResources().cssComponents().pressed());
                if (monthList.isShowing()) {
                    monthList.hide();
                } else {
                    monthList.showUnder(monthLabel, true);
                }
            }
        });

        /*инициализируем надпись года*/
        yearLabel.setText((DateUtil.getCurrentDate().getYear() + DateUtil.YEAR_OFFSET) + "");
        yearLabel.setStyleName(SynergyComponents.getResources().cssComponents().bigText());
        yearLabel.getElement().getStyle().setCursor(Style.Cursor.POINTER);


        EventBus bus = new SimpleEventBus();
        yearsList = new DropDownList<Integer>();
        yearsList.setMinWidth(Constants.YEAR_LIST_WIDTH);
        yearsList.addAutoHidePartner(yearLabel.getElement());

        yearsList.addDaggerItemSelectionHandler(new MenuItemSelection.Handler<Integer>() {
            @Override
            public void onItemSelection(MenuItemSelection<Integer> event) {
                yearLabel.setText(event.getItem().getUserValue() + "");
                yearsList.hide();
                Date year = new Date(picker.currentDate.getTime());
                year.setYear(event.getItem().getUserValue() - DateUtil.YEAR_OFFSET);
                picker.setCurrentDate(year, false);
            }
        });

        for (int i = DateUtil.getCurrentDate().getYear() - 90; i < DateUtil.getCurrentDate().getYear() + 10; i++) {
            yearsList.addItem(new MenuItem<Integer>(i + DateUtil.YEAR_OFFSET, (i + DateUtil.YEAR_OFFSET) + ""));
        }
        yearsList.selectItem(yearsList.get(DateUtil.getCurrentDate().getYear() + DateUtil.YEAR_OFFSET), true, false);

        yearLabel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                event.stopPropagation();
                removeStyleName(SynergyComponents.getResources().cssComponents().pressed());
                if (yearsList.isShowing()) {
                    yearsList.hide();
                } else {
                    yearsList.showUnder(yearLabel, true);
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
