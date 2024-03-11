package ru.ac.uniyar.imageprocessing.ui.components;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.server.StreamResource;
import ru.ac.uniyar.imageprocessing.model.ImageContainer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class ImageField extends CustomField<ImageContainer> {
    private ImageContainer container;
    private ByteArrayOutputStream outputStream;
    private final Image currentImage;
    private final Upload upload;

    public ImageField(String caption) {
        setLabel(caption);
        currentImage = new Image();
        currentImage.setAlt("image");
        currentImage.setMaxHeight("100px");
        currentImage.getStyle().set("margin-right", "15px");
        currentImage.setVisible(false);

        upload = new Upload(this::receiveUpload);
        upload.getStyle().set("flex-grow", "1");

        upload.addSucceededListener(this::uploadSuccess);
        upload.addFailedListener(e -> setFailed(e.getReason().getMessage()));
        upload.addFileRejectedListener(e -> setFailed(e.getErrorMessage()));

        upload.setAcceptedFileTypes("image/*");
        upload.setMaxFiles(1);
        upload.setMaxFileSize(1024 * 1024);

        getStyle().set("width", "100%");

        Div wrapper = new Div();
        wrapper.add(currentImage, upload);
        wrapper.getStyle().set("display", "flex");
        add(wrapper);
    }

    @Override
    protected ImageContainer generateModelValue() {
        return container;
    }

    @Override
    protected void setPresentationValue(ImageContainer imageContainer) {
        container = imageContainer;
        updateImage();
    }

    private OutputStream receiveUpload(String imageName, String type) {
        setInvalid(false);
        container = new ImageContainer();
        container.setName(imageName);
        container.setType(type);

        outputStream = new ByteArrayOutputStream();
        return outputStream;
    }

    private void uploadSuccess(SucceededEvent e) {
        container.setValue(outputStream.toByteArray());
        setModelValue(container, true);
        updateImage();
        upload.getElement().executeJs("this.files=[]");
    }

    private void setFailed(String message) {
        setInvalid(true);
        setErrorMessage(message);
    }

    private void updateImage() {
        if (container != null && container.getValue() != null) {
            currentImage.setSrc(new StreamResource("image", () -> new ByteArrayInputStream(container.getValue())));
            currentImage.setVisible(true);
        } else {
            currentImage.setSrc("");
            currentImage.setVisible(false);
        }
    }
}
