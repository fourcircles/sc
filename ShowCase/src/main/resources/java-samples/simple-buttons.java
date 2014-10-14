final FlowPanel simpleButtonPanel = new FlowPanel();

        simpleButtonPanel.getElement().getStyle().setPadding(10, Style.Unit.PX);

final SimpleButton simpleButton = new SimpleButton(SCMessages.i18n().tr("Простая кнопка"));
        simpleButton.setWidth("140px");
        simpleButton.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        simpleButtonPanel.add(simpleButton);

        simpleButton.addClickHandler(new ClickHandler() {
@Override
public void onClick(ClickEvent event) {
        showCode(SCResources.IMPL.buttons().getText());
        codeComponentName.setText(simpleButton.getText());
        }
        });

        SimpleButton simpleButton1 = new SimpleButton(SCMessages.i18n().tr("Неактивная кнопка"));
        simpleButton1.setEnabled(false);
        simpleButton1.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        simpleButton1.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        simpleButtonPanel.add(simpleButton1);

        SimpleButton simpleButton2 = new SimpleButton(SCMessages.i18n().tr("Кнопка с кликом"));
        simpleButton2.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        simpleButton2.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        simpleButtonPanel.add(simpleButton2);
        simpleButton2.addClickHandler(new ClickHandler() {
@Override
public void onClick(ClickEvent event) {
        Window.alert(SCMessages.i18n().tr("Кнопка была нажата!"));
        }
        });

        ContextMenu menuForSimple = createSimpleMenu();
        ContextMenuButton simpleButton4 = new ContextMenuButton(SCMessages.i18n().tr("Кнопка с меню"));
        simpleButton4.addClickHandler(new ClickHandler() {
@Override
public void onClick(ClickEvent event) {
        Window.alert(SCMessages.i18n().tr("Кнопка с меню была нажата!"));
        }
        });
        simpleButton4.setWidth("140px");
        simpleButton4.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        simpleButton4.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        simpleButtonPanel.add(simpleButton4);
        simpleButton4.setContextMenu(menuForSimple);

        return simpleButtonPanel;