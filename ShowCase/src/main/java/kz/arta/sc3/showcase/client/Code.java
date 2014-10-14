package kz.arta.sc3.showcase.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import kz.arta.synergy.components.client.scroll.ArtaScrollPanel;
import kz.arta.synergy.components.style.client.Color;

/**
 * User: vsl
 * Date: 13.10.14
 * Time: 16:26
 *
 * Компонент для отображения кода
 */
public class Code extends Composite {
    /**
     * Цвет фона
     */
    private static final Color BACKGROUND_COLOR = new Color("#f8f8f8");

    /**
     * Скролл
     */
    private ArtaScrollPanel scroll;

    /**
     * Код
     */
    private String text;

    /**
     * Элемент для кода
     */
    private Element codeElement;

    public Code() {
        scroll = new ArtaScrollPanel();
        initWidget(scroll);
        scroll.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
        scroll.getElement().getStyle().setBackgroundColor(BACKGROUND_COLOR.hex());

        CodeInner code = new CodeInner();
        code.getElement().getStyle().setWidth(100, Style.Unit.PCT);

        scroll.setWidget(code);

        Element preElement = code.getElement();
        codeElement = code.getElement().getFirstChildElement();
    }

    /**
     * Обновляет скролл
     */
    public void updateScroll() {
        scroll.onResize();
    }

    private static native Element createElement()
    /*-{
        var pre = document.createElement('pre');
        var code = document.createElement('code');
        pre.appendChild(code);
        return pre;
    }-*/;

    private static native void setText(Element element, String text)
    /*-{
        element.innerHTML = text;
        $wnd.hljs.highlightBlock(element);
    }-*/;

    /**
     * Изменить текст компонента
     * @param text новый текст
     */
    public void setText(String text) {
        setText(codeElement, text);
        scroll.onResize();
    }

    private class CodeInner extends SimplePanel {
        private CodeInner() {
            super(createElement());
        }
    }
}
