package kz.arta.synergy.components.client.menu.filters;

import kz.arta.synergy.components.client.menu.MenuItem;
import kz.arta.synergy.components.client.menu.events.HasFilterUpdateHandlers;

/**
 * User: vsl
 * Date: 06.08.14
 * Time: 12:29
 *
 * Фильтр для выпадающего списка.
 * При изменении параметров генерирует событие FilterUpdateEvent
 */
public interface ListFilter extends HasFilterUpdateHandlers{
    /**
     * Включать ли элемент в список
     * @param item элемент
     */
    boolean include(MenuItem<?> item);
}
