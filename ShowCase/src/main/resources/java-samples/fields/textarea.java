import kz.arta.sc3.showcase.client.resources.Messages;
import kz.arta.synergy.components.client.input.ArtaTextArea;

public class Sample {
    public static void main(String[] args) {
        final ArtaTextArea textAreaSize = new ArtaTextArea(false);
        textAreaSize.setPlaceHolder(Messages.i18n().tr("Многострочное поле ввода с заданным размером"));
        textAreaSize.setPixelSize(300, 300);
        root.add(textAreaSize);
    }
}

