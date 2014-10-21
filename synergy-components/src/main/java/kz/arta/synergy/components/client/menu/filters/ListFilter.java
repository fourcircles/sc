package kz.arta.synergy.components.client.menu.filters;

import kz.arta.synergy.components.client.dagger.DaggerItem;
import kz.arta.synergy.components.client.menu.DropDownList;
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
    boolean include(DropDownList.Item item);

    boolean include(DaggerItem<?> item);
}
