package kz.arta.synergy.components.client.tree;

import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.client.resources.Messages;

/**
 * User: user
 * Date: 12.11.14
 * Time: 14:34
 * Дерево избранное
 */
public class FavoriteTree extends Tree {

    /**
     * Корневой элемент
     */
    private TreeItem rootItem;

    public FavoriteTree(Tree parentTree, boolean white) {
        super(false, white);
        init();
    }
    public FavoriteTree(Tree parentTree) {
        this(parentTree, true);
    }

    /**
     * инициализируем дерево с пустым узлом "Избранное"
     */
    private void init() {
        rootItem = addItem(Messages.i18n().tr("Избранное"));
        rootItem.setIcon(ImageResources.IMPL.favouriteFolder());
        addStyleName(SynergyComponents.getResources().cssComponents().treeFavorite());
    }

    public TreeItem getRootItem() {
        return rootItem;
    }

}
