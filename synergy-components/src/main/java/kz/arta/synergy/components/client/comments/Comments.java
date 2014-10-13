package kz.arta.synergy.components.client.comments;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import kz.arta.synergy.components.client.comments.events.DeleteCommentEvent;
import kz.arta.synergy.components.client.scroll.ArtaScrollPanel;
import kz.arta.synergy.components.client.theme.ColorType;
import kz.arta.synergy.components.style.client.Colors;
import kz.arta.synergy.components.style.client.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * User: vsl
 * Date: 26.09.14
 * Time: 14:42
 *
 * Панель для отображения комментариев (без элемента для ввода)
 */
public class Comments extends Composite {
    /**
     * Корневой скролл
     */
    private ArtaScrollPanel scroll;

    /**
     * Корневой элемент
     */
    private FlowPanel root;

    /**
     * Список комментариев
     */
    private List<Comment> comments;
    /**
     * Список видов для комментариев
     */
    private List<CommentUI> commentsUI;

    /**
     * Отображается ли скролл-бар.
     * В зависимости от этого меняются отступы для комментариев.
     */
    private boolean hasScroll;

    /**
     * Пустая панель комментариев
     */
    public Comments() {
        this(null, false);
    }

    /**
     * @param comments после создания панели добавить комментарии из списка
     * @param dark темная ли панель комментариев
     */
    public Comments(List<Comment> comments, boolean dark) {
        scroll = new ArtaScrollPanel(dark ? ColorType.BLACK : ColorType.WHITE);
        initWidget(scroll);
        scroll.getElement().getStyle().setBackgroundColor(dark ? Colors.navigatorBG.hex() : Colors.whiteBG.hex());


        root = new FlowPanel();
        root.getElement().getStyle().setPosition(Style.Position.RELATIVE);

        scroll.setWidget(root);

        this.comments = new ArrayList<Comment>();
        commentsUI = new ArrayList<CommentUI>();

        if (comments != null) {
            for (Comment comment : comments) {
                addComment(comment);
            }
        }
    }

    public Comments(List<Comment> comments) {
        this(comments, false);
    }

    public Comments(boolean dark) {
        this(null, dark);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        Scheduler.get().scheduleDeferred(new Command() {
            @Override
            public void execute() {
                root.getElement().getStyle().setWidth(scroll.getOffsetWidth(), Style.Unit.PX);
                updateScrollOffset();
            }
        });
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            //CustomScrollPanel выставляет свои отступы при этом действии
            //без него - при RTL будет неправильное смещение
            root.getElement().getStyle().setLeft(-0.1, Style.Unit.PX);
        }
    }

    /**
     * Создать новый вид для комментария
     * @param comment комментарий
     */
    private CommentUI createCommentUI(Comment comment) {
        CommentUI ui = new CommentUI(comment);

        //отступ при наличии скролла
        updateCommentOffset(ui);

        ui.addDeleteHandler(new DeleteCommentEvent.Handler() {
            @Override
            public void onDeleteComment(DeleteCommentEvent event) {
                removeComment(event.getComment());
            }
        });
        return ui;
    }

    /**
     * Добавить новый комментарий на заданную позицию
     * @param comment комментарий
     * @param beforeIndex новая позиция
     */
    public void addComment(final Comment comment, int beforeIndex) {
        comments.add(beforeIndex, comment);

        CommentUI ui = createCommentUI(comment);
        root.insert(ui, beforeIndex);
        commentsUI.add(beforeIndex, ui);

        //возможно появление скролла
        updateScrollOffset();
    }

    /**
     * Добавить комментарий
     * @param comment комментарий
     */
    public void addComment(final Comment comment) {
        addComment(comment, comments.size());
    }

    /**
     * Обновляет отступы вида комментария в зависимости от наличия скролла
     */
    private void updateCommentOffset(CommentUI commentUI) {
        int margin = 0;
        if (hasScroll) {
            margin = Constants.SCROLL_BAR_WIDTH;
        }
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            commentUI.getElement().getStyle().setMarginLeft(margin, Style.Unit.PX);
        } else {
            commentUI.getElement().getStyle().setMarginRight(margin, Style.Unit.PX);
        }
    }

    /**
     * Проверяет наличие скроллбара и изменяет отступы для корректного отображения
     */
    private void updateScrollOffset() {
        scroll.onResize();
        if (isAttached()) {
            boolean newHasScroll = scroll.getOffsetHeight() < root.getOffsetHeight();
            if (this.hasScroll != newHasScroll) {
                this.hasScroll = newHasScroll;

                for (CommentUI commentUI : commentsUI) {
                    updateCommentOffset(commentUI);
                }
            }
        }
    }

    /**
     * Удалить первый комментарий равный заданному
     * @param comment комментарий
     */
    public void removeComment(Comment comment) {
        removeComment(comments.indexOf(comment));
    }

    /**
     * Удалить комментарий на заданной позиции
     * @param index позиция
     */
    public void removeComment(int index) {
        comments.remove(index);
        root.remove(commentsUI.get(index));
        commentsUI.remove(index);

        updateScrollOffset();
    }

    /**
     * Вызывается при внешнем изменении размеров элемента
     */
    public void onResize() {
        scroll.onResize();
        updateScrollOffset();
    }
}
