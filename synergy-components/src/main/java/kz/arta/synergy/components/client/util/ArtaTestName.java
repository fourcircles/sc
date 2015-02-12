package kz.arta.synergy.components.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;

/**
 * User: vsl
 * Date: 2/3/15
 * Time: 11:43 AM
 * 
 * Добавляет аттрибут для локаторов тестов.
 * В будущем можно добавить через deffered-binding dummy-вариант,
 * который ничего не будет делать. 
 */
public class ArtaTestName {
    private static final String ATTRIBUTE = "arta-test";
    private static final ArtaTestName instance = GWT.create(ArtaTestName.class);
    
    private ArtaTestName() {
        // singleton
    }
    
    public static ArtaTestName getInstance() {
        return instance;
    }
    
    public void setName(Element element, String name) {
        if (name != null) {
            element.setAttribute(ATTRIBUTE, name);
        }
    }
}
