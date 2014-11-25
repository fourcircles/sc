import kz.arta.synergy.components.client.input.FilesPanel;
import kz.arta.synergy.components.client.input.events.NewFilesEvent;

public class Sample {
    public static void main(String[] args) {
        FilesPanel files = new FilesPanel();
        files.addNewFilesHandler(new NewFilesEvent.Handler() {
            @Override
            public void onNewFiles(NewFilesEvent event) {
                //добавлены новые файлы
            }
        });
    }
}