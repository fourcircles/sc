package kz.arta.synergy.components.client.comments;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.HasDirection;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.i18n.shared.DirectionEstimator;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.comments.events.InputChangeEvent;
import kz.arta.synergy.components.client.comments.events.NewCommentEvent;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.client.scroll.ArtaScrollPanel;
import kz.arta.synergy.components.client.util.ArtaHasText;
import kz.arta.synergy.components.client.util.Utils;
import kz.arta.synergy.components.style.client.Constants;

/**
 * User: vsl
 * Date: 26.09.14
 * Time: 15:59
 *
 * Элемент для ввода комментария.
 * Увеличивает высоту по мере увеличения количества линий.
 *
 * Для правильного вычисления используется дополнительный элемент (один на все поля ввода).
 */
public class CommentInput extends Composite implements ArtaHasText, HasResizeHandlers {
    /**
     * Стиль шрифта
     */
    private static final String FONT = SynergyComponents.resources.cssComponents().mainText();
    /**
     * Максимальное количество линий, при которых не отображается скролл-бар
     */
    private static final int MAX_LINES = 10;
    /**
     * Элемент ввода
     */
    private TextArea textArea;

    /**
     * Элемент для вычисления высоты текста
     */
    private static TextArea mirror;
    /**
     * Корневой скролл
     */
    private final ArtaScrollPanel scroll;
    /**
     * Корневая панель
     */
    private final FlowPanel root;
    /**
     * Отображен ли скролл
     */
    private boolean hasScroll = false;

    /**
     * Галочка для принятия комментария
     */
    private Image acceptImage;

    /**
     * Сумма отступов для текста
     */
    private int textPadding = Constants.COMMON_INPUT_PADDING * 3 + Constants.STD_ICON_WIDTH;

    /**
     * Создает элемент, по которому считается необходимая высота текста
     */
    private static void initMirror() {
        if (mirror == null) {
            mirror = new TextArea();
            RootPanel.get().add(mirror);
            mirror.setStyleName(FONT);

            Style style = mirror.getElement().getStyle();

            style.setPadding(0, Style.Unit.PX);
            style.setPaddingLeft(Constants.COMMON_INPUT_PADDING, Style.Unit.PX);
            style.setPaddingRight(0, Style.Unit.PX);
            style.setMargin(0, Style.Unit.PX);
            style.setBorderStyle(Style.BorderStyle.NONE);
            style.setOverflow(Style.Overflow.HIDDEN);

            style.setProperty("resize", "none");
            style.setProperty("wordBreak", "break-all");
            style.setLineHeight(Constants.COMMENT_INPUT_LINE_HEIGHT, Style.Unit.PX);

            style.setPosition(Style.Position.ABSOLUTE);
            style.setTop(0, Style.Unit.PX);
            style.setLeft(0, Style.Unit.PX);
            style.setHeight(0, Style.Unit.PX);
        }
    }

    public CommentInput() {
        scroll = new ArtaScrollPanel();
        initWidget(scroll);
        scroll.getElement().getStyle().setFontSize(0, Style.Unit.PX);

        root = new FlowPanel();

        scroll.setWidget(root);
        root.setStyleName(SynergyComponents.resources.cssComponents().commentInput());

        textArea = new TextArea();
        textArea.setDirectionEstimator(new DirectionEstimator() {
            @Override
            public HasDirection.Direction estimateDirection(String str) {
                return LocaleInfo.getCurrentLocale().isRTL() ? HasDirection.Direction.RTL : HasDirection.Direction.LTR;
            }
        });
        textArea.setStyleName(SynergyComponents.resources.cssComponents().mainText());
        root.add(textArea);

        initMirror();

        acceptImage = new Image(ImageResources.IMPL.post());
        acceptImage.setStyleName(SynergyComponents.resources.cssComponents().commentInputAccept());

        root.add(acceptImage);

        InputChangeEvent.addInputHandler(getElement(), new InputChangeEvent.Handler() {
            @Override
            public void onInputChange(InputChangeEvent event) {
                textChanged();
            }
        });

        //ie9 не создает событие "input" при удалении символов, поэтому
        //надо слушать KeyUpEvent.
        if (Window.Navigator.getAppVersion().contains("MSIE")) {
            textArea.addKeyUpHandler(new KeyUpHandler() {
                @Override
                public void onKeyUp(KeyUpEvent event) {
                    textChanged();
                }
            });
        }

        acceptImage.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                postComment();
            }
        });

        textArea.addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                if (event.isControlKeyDown() && event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    postComment();
                }
            }
        });
    }

    /**
     * Метод вызывается при добавлении комментария
     */
    private void postComment() {
        fireEvent(new NewCommentEvent(textArea.getValue()));
        setValue("");
    }

    /**
     * Вызывается при изменении размеров скролла
     */
    private void resize() {
        int offsetHeight = Constants.COMMENT_INPUT_LINE_HEIGHT + Constants.COMMON_INPUT_PADDING * 2;
        scroll.getElement().getStyle().setHeight(offsetHeight, Style.Unit.PX);

        int width = scroll.getOffsetWidth();
        root.getElement().getStyle().setWidth(width, Style.Unit.PX);
        width -= Constants.COMMON_INPUT_PADDING * 2;
        textArea.getElement().getStyle().setWidth(width, Style.Unit.PX);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        int offsetHeight = Constants.COMMENT_INPUT_LINE_HEIGHT + Constants.COMMON_INPUT_PADDING * 2;
        scroll.getElement().getStyle().setHeight(offsetHeight, Style.Unit.PX);
        textArea.getElement().getStyle().setHeight(offsetHeight, Style.Unit.PX);

        int width = scroll.getOffsetWidth();
        root.getElement().getStyle().setWidth(width, Style.Unit.PX);

        textArea.getElement().getStyle().setWidth(width, Style.Unit.PX);

        root.getElement().getStyle().setPosition(Style.Position.RELATIVE);
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            //CustomScrollPanel выставляет свои отступы при этом действии
            //без него при RTL будет неправильное смещение
            root.getElement().getStyle().setLeft(-0.1, Style.Unit.PX);
        }
    }

    /**
     * Изменяет отступы при появлении скролла
     * @param hasScroll отображен ли скролл
     */
    private void updateOffsetForScroll(boolean hasScroll) {
        int imagePadding = Constants.COMMON_INPUT_PADDING;
        int sideTextPadding = Constants.STD_ICON_WIDTH + Constants.COMMON_INPUT_PADDING * 2; //ширина иконки

        if (hasScroll) {
            imagePadding += Constants.SCROLL_BAR_WIDTH;
            sideTextPadding += Constants.SCROLL_BAR_WIDTH;
        }

        this.textPadding = sideTextPadding + Constants.COMMON_INPUT_PADDING;

        if (LocaleInfo.getCurrentLocale().isRTL()) {
            acceptImage.getElement().getStyle().setPaddingLeft(imagePadding, Style.Unit.PX);
            textArea.getElement().getStyle().setPaddingLeft(sideTextPadding, Style.Unit.PX);
        } else {
            acceptImage.getElement().getStyle().setPaddingRight(imagePadding, Style.Unit.PX);
            textArea.getElement().getStyle().setPaddingRight(sideTextPadding, Style.Unit.PX);
        }
    }

    /**
     * Вызывается при изменении текста
     */
    private void textChanged() {
        //здесь нужна точная ширина
        double width = Utils.getPreciseWidth(textArea.getElement());
        width -= textPadding;

        //левый или правый padding не имеет значения, главное сумма горизонтальных отступов текста
        mirror.getElement().getStyle().setPaddingLeft(textPadding, Style.Unit.PX);
        mirror.getElement().getStyle().setPaddingRight(0, Style.Unit.PX);

        mirror.getElement().getStyle().setWidth(width, Style.Unit.PX);
        mirror.setValue(textArea.getValue());

        int height = mirror.getElement().getScrollHeight();
        int oldHeight = textArea.getOffsetHeight() - Constants.COMMON_INPUT_PADDING * 2;
        int lines = height / Constants.COMMENT_INPUT_LINE_HEIGHT;

        if (lines <= MAX_LINES) {
            if (this.hasScroll) {
                hasScroll = false;
                updateOffsetForScroll(false);
                textChanged();
            }
            textArea.getElement().getStyle().setHeight(height + Constants.COMMON_INPUT_PADDING * 2, Style.Unit.PX);
            scroll.getElement().getStyle().setHeight(height + Constants.COMMON_INPUT_PADDING * 2, Style.Unit.PX);
        } else {
            if (!this.hasScroll) {
                hasScroll = true;
                updateOffsetForScroll(true);
                textChanged();
            }
            textArea.getElement().getStyle().setHeight(height + Constants.COMMON_INPUT_PADDING * 2, Style.Unit.PX);
            scroll.getElement().getStyle().setHeight(MAX_LINES * Constants.COMMENT_INPUT_LINE_HEIGHT + Constants.COMMON_INPUT_PADDING * 2, Style.Unit.PX);
        }

        if (oldHeight != height) {
            scroll.setVerticalScrollPosition(scroll.getMaximumVerticalScrollPosition());
            ResizeEvent.fire(this, -1, height);
        }
    }

    @Override
    public String getFontStyle() {
        return null;
    }

    @Override
    public String getText() {
        return textArea.getText();
    }

    @Override
    public void setText(String text) {
        mirror.setText(Utils.parseComment(text));
    }

    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        return textArea.addKeyUpHandler(handler);
    }

    public String getValue() {
        return textArea.getValue();
    }

    public void setValue(String value) {
        textArea.setValue(value);
        textChanged();
    }

    @Override
    public HandlerRegistration addResizeHandler(ResizeHandler handler) {
        return addHandler(handler, ResizeEvent.getType());
    }

    public HandlerRegistration addNewCommentHandler(NewCommentEvent.Handler handler) {
        return addHandler(handler, NewCommentEvent.getType());
    }
}
