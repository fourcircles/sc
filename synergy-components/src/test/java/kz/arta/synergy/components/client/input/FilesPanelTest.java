package kz.arta.synergy.components.client.input;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.style.client.resources.ComponentResources;
import kz.arta.synergy.components.style.client.resources.CssComponents;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class FilesPanelTest {
    @GwtMock ComponentResources resources;
    @Mock CssComponents cssComponents;
    private FilesPanel filesPanel;

    @Before
    public void setUp() {
        when(resources.cssComponents()).thenReturn(cssComponents);
        new SynergyComponents().onModuleLoad();

        filesPanel = new FilesPanel();
    }

    @Test
    public void testGetFileName() {
        assertEquals("hello.c", filesPanel.getFileName("c:\\dj\\eif\\efiefj\\hello.c"));
        assertEquals("hello.c", filesPanel.getFileName("c:\\dj\\eif\\..\\efiefj\\hello.c"));
        assertEquals("hello.c", filesPanel.getFileName("/dj/eif/hello.c"));
        assertEquals("hello.peter.c", filesPanel.getFileName("/dj/eif/hello.peter.c"));
        assertEquals("hello", filesPanel.getFileName("dj/eif/hello"));
    }

    @Test
    public void testFilesSelected() {
        FilesPanel spy = spy(filesPanel);
        List<String> files1 = Arrays.asList("a", "b", "c");
        doReturn(files1).when(spy).getSelectedFiles();
        spy.filesSelected();

        assertEquals(3, spy.getAddedFiles().size());
        assertTrue(spy.getAddedFiles().containsAll(files1));

        List<String> files2 = Arrays.asList("b", "c", "d");
        doReturn(files2).when(spy).getSelectedFiles();
        spy.filesSelected();

        assertEquals(4, spy.getAddedFiles().size());
        assertTrue(spy.getAddedFiles().contains("d"));
    }
}