package kz.arta.synergy.components.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.*;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.style.client.Constants;

import java.util.List;

/**
 * User: vsl
 * Date: 04.11.14
 * Time: 17:38
 *
 * Уведомление
 */
public class Notification {
    private static final int NOTIFICATION_Z_INDEX = 1500;

    static final String BR = "<br/>";
    static final String LIST_START = "<ul>";
    static final String LIST_END = "</ul>";
    static final String LIST_ITEM_START = "<li>";
    static final String LIST_ITEM_END = "</li>";

    /**
     * Корневой элемент
     */
    protected FlowPanel root;
    /**
     * Иконка
     */
    protected final Image icon;
    /**
     * Надо ли показывать иконку
     */
    protected boolean hasIcon;
    /**
     * Попап уведомления
     */
    protected PopupPanel popup;

    /**
     * Текст уведомления
     */
    protected HTML content;

    /**
     * Wrapper контента.
     */
    protected SimplePanel contentContainer;

    /**
     * @param text текст
     * @param type тип
     */
    public Notification(String text, Type type) {
        this(text, null, type);
    }

    /**
     * @param text текст
     * @param comment комментарий главного сообщения
     * @param messages дополнительные сообщения, которые будут отображены в списке
     * @param type тип
     */
    public Notification(String text, String comment, List<String> messages, Type type) {
        root = new FlowPanel();

        root.setStyleName(SynergyComponents.getResources().cssComponents().notification());
        root.addStyleName(SynergyComponents.getResources().cssComponents().mainText());

        icon = GWT.create(Image.class);
        root.add(icon);

        setIcon(type);
        setTextColor(type);

        content = GWT.create(HTML.class);
        content.setHTML(toSafeHtml(text, comment, messages));

        contentContainer = new SimplePanel(content);
        contentContainer.setStyleName(SynergyComponents.getResources().cssComponents().notificationText());
        root.add(contentContainer);

        popup = new PopupPanel(false);
        popup.getElement().getStyle().setZIndex(NOTIFICATION_Z_INDEX);
        popup.setStyleName("");
        popup.setModal(false);
        popup.setWidget(root);
    }

    public Notification(String text, List<String> messages, Type type) {
        this(text, null, messages, type);
    }

    @SuppressWarnings("UnusedDeclaration")
    public Notification(ServerResult result) {
        this(result.getErrorMessage(), result.getComment(), result.getMessages(),
                result.getErrorCode() == ServerResult.ErrorCode.NO_ERROR ? Type.SUCCESS : Type.FAILURE);
    }

    /**
     * Преобразовывает {@link kz.arta.synergy.components.client.ServerResult} в safehtml для отображения
     * в уведомлении
     */
    @SuppressWarnings("UnusedDeclaration")
    static SafeHtml toSafeHtml(ServerResult serverResult) {
        return toSafeHtml(serverResult.getErrorMessage(),
                serverResult.getComment(),
                serverResult.getMessages());
    }

    /**
     * {@link #toSafeHtml(String, String, java.util.List)}
     */
    @SuppressWarnings("UnusedDeclaration")
    static SafeHtml toSafeHtml(String message) {
        return toSafeHtml(message, null, null);
    }

    /**
     * @param message главное сообщение
     * @param comment комментарий главного сообщения
     * @param messages дополнительные сообщения, которые будут отображены в списке
     * @return safehtml сообщения и списока, который содержит дополнительные сообщения
     */
    static SafeHtml toSafeHtml(String message, String comment, List<String> messages) {
        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        builder.appendEscaped(message);

        if (comment != null && !comment.isEmpty()) {
            builder.appendHtmlConstant(BR);
            builder.appendEscaped(comment);
        }

        if (messages != null && !messages.isEmpty()) {
            builder.appendHtmlConstant(BR);
            builder.appendHtmlConstant(BR);
            builder.appendHtmlConstant(LIST_START);
            for (String listItem : messages) {
                builder.appendHtmlConstant(LIST_ITEM_START);
                builder.appendEscaped(listItem);
                builder.appendHtmlConstant(LIST_ITEM_END);
            }
            builder.appendHtmlConstant(LIST_END);
        }
        return builder.toSafeHtml();
    }

    /**
     * Задает правильный цвет шрифта в соответствии с типом уведомления
     *
     * @param type тип уведомления
     */
    private void setTextColor(Type type) {
        switch (type) {
            case SUCCESS:
                root.addStyleName(SynergyComponents.getResources().cssComponents().success());
                break;
            case WARNING:
                root.addStyleName(SynergyComponents.getResources().cssComponents().warning());
                break;
            case FAILURE:
                root.addStyleName(SynergyComponents.getResources().cssComponents().failure());
        }
    }

    /**
     * Задает правильную иконку или скрывает ее в соответствии с типом уведомления
     *
     * @param type тип уведомления
     */
    private void setIcon(Type type) {
        ImageResource iconImage = null;
        switch (type) {
            case SUCCESS:
                iconImage = ImageResources.IMPL.notificationSuccess();
                break;
            case QUESTION:
                iconImage = ImageResources.IMPL.notificationQuestion();
                break;
            case WARNING:
                iconImage = ImageResources.IMPL.notificationYellowQuestion();
                break;
            case FAILURE:
                iconImage = ImageResources.IMPL.notificationFailure();
                break;
        }
        hasIcon = iconImage != null;
        if (iconImage == null) {
            icon.getElement().getStyle().setDisplay(Style.Display.NONE);
        } else {
            icon.setResource(iconImage);
        }
    }

    /**
     * Центрирование уведомления
     */
    public void center() {
        popup.center();
    }

    /**
     * Изменяет позицию попапа
     */
    public void setPopupPosition(int x, int y) {
        popup.setPopupPosition(x, y);
    }

    /**
     * {@link com.google.gwt.user.client.ui.PopupPanel#setPopupPositionAndShow(com.google.gwt.user.client.ui.PopupPanel.PositionCallback)}
     * Позволяет задать позицию уведомления в соответствии с размерами (например, это удобно для правильного центрирования)
     *
     * @param callback вызывается методом после выяснения размеров уведомления, обычно этот callback должен использовать {@link #setPopupPosition(int, int)}
     */
    public void setPopupPositionAndShow(PopupPanel.PositionCallback callback) {
        popup.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
        popup.center();
        popup.show();

        align();

        int width = popup.getOffsetWidth();
        int height = popup.getOffsetHeight();
        callback.setPosition(width, height);
        popup.getElement().getStyle().clearVisibility();
    }

    /**
     * Задает правильную ширину контенту.
     */
    protected void align() {
        contentContainer.getElement().getStyle().clearWidth();
        contentContainer.getElement().getStyle().setWidth(content.getOffsetWidth() + 1, Style.Unit.PX);

        verticalAlignImageText();
    }

    /**
     * Вертикально выравнивает иконку и текст
     */
    protected void verticalAlignImageText() {
        if (!hasIcon) {
            return;
        }
        contentContainer.getElement().getStyle().clearMarginTop();
        icon.getElement().getStyle().clearMarginTop();
        int textHeight = contentContainer.getOffsetHeight();
        double delta = Math.abs((double) Constants.NOTIFICATION_ICON_SIZE - textHeight) / 2;
        if (textHeight < Constants.NOTIFICATION_ICON_SIZE) {
            contentContainer.getElement().getStyle().setMarginTop(delta, Style.Unit.PX);
        } else {
            icon.getElement().getStyle().setMarginTop(delta, Style.Unit.PX);
        }
    }

    /**
     * Скрыть уведомление
     */
    public void hide() {
        popup.hide();
    }

    /**
     * Выясняет показывается ли уведомление
     *
     * @return <code>true</code> если уведомление показывается
     */
    public boolean isShowing() {
        return popup.isShowing();
    }

    /**
     * Тип уведомления
     */
    public enum Type {
        /**
         * Успешная операция
         */
        SUCCESS,
        /**
         * Неудачная операция
         */
        FAILURE,
        /**
         * Вопрос
         */
        QUESTION,
        /**
         * Предупреждение
         */
        WARNING,
        /**
         * Иконки нет, обычный шрифт
         */
        NEUTRAL
    }
}
