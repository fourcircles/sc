package kz.arta.synergy.components.client.util;

import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;

/**
 * User: vsl
 * Date: 20.10.14
 * Time: 16:24
 *
 * Хэндлер для события движения мыши с логикой для исключения событий небольшого
 * движения мыши (многие мыши немного двигаются без участия пользователя).
 */
public abstract class ThickMouseMoveHandler implements MouseMoveHandler {
    /**
     * Предел по умолчанию
     */
    private static final int DEFAULT_THRESHOLD = 15;

    /**
     * Предел хэндлера
     */
    private int threshold;

    /**
     * Задана ли начальная позиция мыши
     */
    private boolean started = false;

    /**
     * Координаты мыши предыдущего события
     */
    private int oldX;
    private int oldY;

    public ThickMouseMoveHandler(int threshold) {
        this.threshold = threshold;
    }

    public ThickMouseMoveHandler() {
        this(DEFAULT_THRESHOLD);
    }

    /**
     * Показывает преодолела ли мышь необходимое расстояние с предыдущего события
     * @param x координата мыши
     * @param y координата мыши
     */
    protected boolean overThreshold(int x, int y) {
        if (!started) {
            oldX = x;
            oldY = y;
            started = true;
        }
        int distance = Math.abs(oldX - x) +
                Math.abs(oldY - y);
        if (distance > threshold) {
            started = false;
            return true;
        }
        return false;
    }
    private static class Move extends MouseMoveEvent {

    }
}

