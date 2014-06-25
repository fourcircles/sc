package kz.arta.sc3.showcase.client.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Tree;

/**
 * User: vsl
 * Date: 23.06.14
 * Time: 15:59
 */
public interface TreeResources extends Tree.Resources {
    public TreeResources IMPL = (TreeResources) GWT.create(TreeResources.class);

    @Override
    ImageResource treeClosed();

    @Override
    ImageResource treeLeaf();

    @Override
    ImageResource treeOpen();
}
