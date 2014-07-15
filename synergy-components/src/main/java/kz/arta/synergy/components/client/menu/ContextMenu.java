package kz.arta.synergy.components.client.menu;

import com.google.gwt.user.client.ui.FlowPanel;
import kz.arta.synergy.components.client.SynergyComponents;

/**
 * User: vsl
 * Date: 09.07.14
 * Time: 15:14
 */
public class ContextMenu extends MenuBase {
    private final static MenuItem SEPARATOR = new MenuItem(null);

    @Override
    protected void selectItem(int index) {
        if (items.get(index) == SEPARATOR) {
            return;
        }
        super.selectItem(index);
    }

    private boolean isSeparator(MenuItem item) {
        return item == SEPARATOR;
    }

    private boolean isSeparator(int index) {
        return isSeparator(items.get(index));
    }

    public void addSeparator() {
        items.add(SEPARATOR);
        FlowPanel separatorPanel =  new FlowPanel();
        separatorPanel.setStyleName(SynergyComponents.resources.cssComponents().menuSeparator());
        panel.add(separatorPanel);
    }

    @Override
    protected int getFirst() {
        return getNext(-1);
    }

    @Override
    protected int getLast() {
        return getPrevious(items.size());
    }

    @Override
    protected int getNext() {
        return getNext(selectedIndex);
    }

    private int getNext(int start) {
        int i = start + 1;
        if (i < 0) {
            return -1;
        }
        while (i < items.size() && isSeparator(i)) {
            i++;
        }
        if (i == items.size()) {
            i = 0;
            while (i < start && isSeparator(i)) {
                i++;
            }
            return i >= start ? -1 : i;
        } else {
            return i;
        }
    }

    @Override
    protected int getPrevious() {
        return getPrevious(selectedIndex);
    }

    private int getPrevious(int start) {
        int i = start - 1;
        if (i >= items.size()) {
            return -1;
        }
        while (i >= 0 && isSeparator(i)) {
            i--;
        }
        if (i == -1) {
            i = items.size() - 1;
            while (i > start && isSeparator(i)) {
                i--;
            }
            return i <= start ? -1 : i;

        } else {
            return i;
        }
    }

    @Override
    protected String getMainStyle() {
        return SynergyComponents.resources.cssComponents().contextMenu();
    }


}
