import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import kz.arta.sc3.showcase.client.resources.SCMessages;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.button.SimpleButton;
import kz.arta.synergy.components.client.tabs.TabPanel;

public class Sample {

    public static void main(String[] args) {
        final TabPanel tabPanel = new TabPanel();
        tabPanel.setWidth("450px");
        tabPanel.setHeight("300px");

        // добавляет таб
        addSimpleTab(tabPanel, SCMessages.i18n().tr("Вкладка") + " 1");
        addSimpleTab(tabPanel, SCMessages.i18n().tr("Вкладка") + " 2");
        addSimpleTab(tabPanel, SCMessages.i18n().tr("Вкладка") + " 3");

        // выделяет таб с индексом 1
        tabPanel.selectTab(1);

        SimpleButton addTab = new SimpleButton(SCMessages.i18n().tr("Добавить вкладку"));

        // при нажатии на кнопку добавляется новая вкладка
        addTab.addClickHandler(new ClickHandler() {
            private int tabCount = 3;
            @Override
            public void onClick(ClickEvent event) {
                addSimpleTab(tabPanel, SCMessages.i18n().tr("Вкладка") + " " + ++tabCount);
            }
        });
    }

    /**
     * Добавляет вкладку в которой содержится только ее текст
     * @param tabPanel панель вкладок
     * @param text текст вкладки и текст контента
     */
    private void addSimpleTab(TabPanel tabPanel, String text) {
        Label label = new Label(text);
        label.setStyleName(SynergyComponents.getResources().cssComponents().mainTextBold());
        SimplePanel panel = new SimplePanel(label);
        panel.getElement().getStyle().setPadding(10, Style.Unit.PX);

        tabPanel.addTab(text, panel);
    }
}

