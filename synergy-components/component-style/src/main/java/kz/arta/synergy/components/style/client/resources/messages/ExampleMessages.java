package kz.arta.synergy.components.style.client.resources.messages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.LocalizableResource;
import com.google.gwt.i18n.client.Messages;

/**
 * User: vsl
 * Date: 26.06.14
 * Time: 14:15
 */
@LocalizableResource.DefaultLocale("ru")
public interface ExampleMessages extends Messages {
    ExampleMessages IMPL = GWT.create(ExampleMessages.class);
    public String navigation();
}
