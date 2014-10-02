package kz.arta.synergy.components.client.input.date.repeat;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.input.tags.ObjectChooser;
import kz.arta.synergy.components.client.input.tags.Tag;
import kz.arta.synergy.components.client.input.tags.events.TagAddEvent;
import kz.arta.synergy.components.client.input.tags.events.TagRemoveEvent;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.client.resources.Messages;

import java.util.Collection;
import java.util.Set;

/**
 * User: vsl
 * Date: 24.09.14
 * Time: 11:50
 *
 * Компонент выбора периода повторения.
 * Имеет несколько режимов работы {@link kz.arta.synergy.components.client.input.date.repeat.RepeatChooser.MODE}
 * При переключении режимов, выбранные периоды не сохраняются.
 */
public class RepeatChooser implements IsWidget, HasEnabled {
    private static final MODE DEFAULT_MODE = MODE.WEEK;

    /**
     * Теги
     */
    private ObjectChooser<RepeatDate> tags;

    /**
     * Текущий режим работы
     */
    private MODE mode;

    /**
     * Элемент выбора периода
     */
    private BaseRepeatChooser chooser;

    private EventBus bus;

    public RepeatChooser() {
        this(DEFAULT_MODE);
    }

    /**
     * @param mode начальный режим работы
     */
    public RepeatChooser(MODE mode) {
        bus = new SimpleEventBus();
        tags = new ObjectChooser<RepeatDate>(bus, ImageResources.IMPL.calendarIcon());
        tags.addStyleName(SynergyComponents.resources.cssComponents().repeatChooser());

        tags.addButtonClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.getNativeButton() != NativeEvent.BUTTON_LEFT) {
                    return;
                }
                if (chooser.isShowing()) {
                    chooser.hide();
                } else {
                    int y = tags.getAbsoluteTop() + tags.getOffsetHeight();
                    if (LocaleInfo.getCurrentLocale().isRTL()) {
                        chooser.show(tags.getAbsoluteLeft(), y);
                    } else {
                        chooser.show(tags.getAbsoluteLeft() + tags.getOffsetWidth(), y);
                    }
                }
            }
        });

        this.mode = mode;
        chooser = createChooser();

        bus.addHandler(TagRemoveEvent.getType(), new TagRemoveEvent.Handler<RepeatDate>() {
            @Override
            public void onTagRemove(TagRemoveEvent<RepeatDate> event) {
                RepeatDate date = event.getTag().getValue();
                getChooser().deselect(date, false);
            }
        });
    }

    /**
     * @return выбранные даты
     */
    public Set<RepeatDate> getSelected() {
        return chooser.getSelectedDates();
    }

    /**
     * Добавляет дату к выбранным
     */
    public void addSelected(RepeatDate repeatDate) {
        if (mode == repeatDate.getMode()) {
            chooser.select(repeatDate, true);
        }
    }

    /**
     * Добавляет даты к выбранным
     */
    public void addSelected(Collection<RepeatDate> repeatDates) {
        for (RepeatDate date : repeatDates) {
            addSelected(date);
        }
    }

    /**
     * Удаляет дату из выбранных
     */
    public void removeSelected(RepeatDate repeatDate) {
        if (mode == repeatDate.getMode()) {
            chooser.deselect(repeatDate, true);
        }
    }

    /**
     * Удаляет даты из выбранных
     */
    public void removeSelected(Collection<RepeatDate> repeatDates) {
        for (RepeatDate date : repeatDates) {
            removeSelected(date);
        }
    }

    /**
     * Возвращает базовый компонент для выбора периода
     */
    private BaseRepeatChooser getChooser() {
        if (chooser == null) {
            chooser = createChooser();
        }
        return chooser;
    }

    /**
     * Создает тэг для даты.
     * @param repeatDate дата
     * @return тэг
     */
    private Tag<RepeatDate> createTag(RepeatDate repeatDate) {
        Tag<RepeatDate> tag = new Tag<RepeatDate>(repeatDate.toString(), repeatDate);
        tag.setBus(bus);
        return tag;
    }

    /**
     * Создает компонент выбора для текущего режима
     * @return
     */
    private BaseRepeatChooser createChooser() {
        BaseRepeatChooser chooser;
        switch (mode) {
            case MONTH:
                chooser = new MonthlyRepeatChooser();
                break;
            case YEAR:
                chooser = new YearlyRepeatChooser();
                break;
            case WEEK:
                chooser = new WeeklyRepeatChooser();
                break;
            default:
                throw new IllegalStateException();
        }
        chooser.addAutoHidePartner(tags.getElement());
        chooser.addValueChangeHandler(new ValueChangeHandler<RepeatDate>() {
            @Override
            public void onValueChange(ValueChangeEvent<RepeatDate> event) {
                RepeatDate value = event.getValue();
                if (tags.contains(value)) {
                    bus.fireEvent(new TagRemoveEvent<RepeatDate>(tags.getTag(value)));
                } else {
                    bus.fireEvent(new TagAddEvent<RepeatDate>(createTag(value)));
                }
            }
        });
        return chooser;
    }

    @Override
    public Widget asWidget() {
        return tags;
    }

    @Override
    public boolean isEnabled() {
        return tags.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        tags.setEnabled(enabled);
    }

    public MODE getMode() {
        return mode;
    }

    /**
     * Изменяет режим работы
     * @param mode новый режим
     */
    public void setMode(MODE mode) {
        clear();

        boolean newMode = false;
        if (this.mode != mode) {
            newMode = true;
        }
        this.mode = mode;
        if (newMode) {
            chooser = createChooser();
        }
    }

    /**
     * Удаляет все выбранный даты
     */
    public void clear() {
        tags.clear();
    }

    /**
     * Возможные режимы выбора периода
     */
    public enum MODE {
        WEEK(Messages.i18n.tr("По дням недели")),
        MONTH(Messages.i18n.tr("По дням месяца")),
        YEAR(Messages.i18n.tr("Ежегодно"));

        private String name;

        MODE(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
