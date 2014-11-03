import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import kz.arta.sc3.showcase.client.ShowCase;
import kz.arta.sc3.showcase.client.resources.Messages;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.button.SimpleButton;
import kz.arta.synergy.components.client.tabs.TabPanel;

public class Sample {

    public static void main(String[] args) {

        FlowPanel root = new FlowPanel();
        root.getElement().getStyle().setPadding(10, Style.Unit.PX);

        SimpleButton addTab = new SimpleButton(Messages.i18n().tr("Добавить вкладку"));
        root.add(addTab);

        SimpleButton addNonclosingTab = new SimpleButton(Messages.i18n().tr("Добавить незакрывающуюся вкладку"));
        root.add(addNonclosingTab);

        final TabPanel localTabPanel = new TabPanel();
        localTabPanel.setWidth("450px");
        localTabPanel.setHeight("300px");

        root.add(localTabPanel);

        // добавляет вкладки
        addSimpleTab(localTabPanel, Messages.i18n().tr("Вкладка") + " 1");
        addSimpleTab(localTabPanel, Messages.i18n().tr("Вкладка") + " 2");
        addSimpleTab(localTabPanel, Messages.i18n().tr("Вкладка") + " 3");

        // выделяет первую вкладку, по умолчанию -- никакая не выбрана
        localTabPanel.selectTab(1);

        addTab.addClickHandler(new ClickHandler() {
            private int tabCount = 3;
            @Override
            public void onClick(ClickEvent event) {
                addSimpleTab(localTabPanel, Messages.i18n().tr("Вкладка") + " " + ++tabCount);
            }
        });

        addNonclosingTab.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                addSimpleTab(localTabPanel, Messages.i18n().tr("Вкладка"), false);
            }
        });

    }

    private void addSimpleTab(TabPanel tabPanel, String text) {
        addSimpleTab(tabPanel, text, true);
    }

    /**
     * Добавляет новую вкладку
     * @param tabPanel панель вкладок
     * @param text текст новой вкладки
     * @param closable можно ли закрыть вкладку
     */
    private void addSimpleTab(TabPanel tabPanel, String text, boolean closable) {
        Label label = new Label(text);
        label.setStyleName(SynergyComponents.getResources().cssComponents().mainTextBold());
        SimplePanel panel = new SimplePanel(label);

        tabPanel.addTab(text, panel, closable);
    }
}

