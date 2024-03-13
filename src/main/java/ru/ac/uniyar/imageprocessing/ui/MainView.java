package ru.ac.uniyar.imageprocessing.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ac.uniyar.imageprocessing.model.ImageContainer;
import ru.ac.uniyar.imageprocessing.model.ProcessType;
import ru.ac.uniyar.imageprocessing.processor.ImageProcessor;
import ru.ac.uniyar.imageprocessing.ui.components.ImageField;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Route
public class MainView extends VerticalLayout {

    Logger logger = LoggerFactory.getLogger(MainView.class);
    Div processResult;
    Div params;
    ProcessType processType;
    FormLayout formLayout;

    public MainView() {
        logger.info("Инициализация страницы");
        init();
    }

    private void init() {
        logger.info("Отрисовка страницы");
        processResult = new Div();
        params = new Div();
        processType = ProcessType.HALFTONE;
        formLayout = new FormLayout();
        formLayout.setWidth("100%");


        ImageField imageField = new ImageField("Выберите изображение для обработки:");

        ComboBox<String> processSelector = getComboBox();

        Button processButton = new Button("Перейти к обработке изображения",
                e -> processImage(imageField.getValue()));
        Button reloadButton = new Button("Обновить", e -> reloadPage());

        Div buttons = new Div();
        buttons.add(processButton, reloadButton);
        buttons.getStyle().set("display", "flex");
        buttons.getStyle().set("justify-content", "space-evenly");

        Div leftControls = new Div();
        leftControls.add(imageField, buttons);

        Div rightControls = new Div();
        rightControls.add(processSelector, params);

        formLayout.add(leftControls, rightControls, processResult);

        add(formLayout);
    }

    private ComboBox<String> getComboBox() {
        ComboBox<String> processSelector = new ComboBox<>("Выберите тип обработки:",
                List.of(ProcessType.HALFTONE.getName(), ProcessType.BINARY.getName(), ProcessType.NEGATIVE.getName(),
                        ProcessType.LOGARITHM.getName(), ProcessType.POWER.getName()));
        processSelector.setValue("Преобразование цветного изображения в полутоновое");
        processSelector.addValueChangeListener(e -> {
            updateProcessType(processSelector.getValue());
            editParams();
        });
        processSelector.addValueChangeListener(e -> editParams());
        processSelector.getStyle().set("width", "100%");

        return processSelector;
    }

    private void processImage(ImageContainer imageContainer) {
        if (imageContainer == null || imageContainer.getValue() == null) {
            Notification.show("Не выбрано изображение для обработки");
            return;
        }

        if (hasInvalidParams()) {
            Notification.show("Параметры обработки изображения введены некорректно");
        } else {
            logger.info("Переход к обработке изображения, выбранный тип обработки: " + processType);

            ImageContainer resultImage = ImageProcessor.processImage(imageContainer, processType, getParams());

            showProcessResult(imageContainer.getValue(), resultImage.getValue(), imageContainer.getCutType());
        }
    }

    private void showProcessResult(byte[] before, byte[] result, String imageType) {
        logger.info("Отображение результата");

        processResult.removeAll();
        processResult.getStyle().set("width", "100%");

        Image beforeImage = new Image(new StreamResource("before.".concat(imageType), () -> new ByteArrayInputStream(before)), "before");
        Image resultImage = new Image(new StreamResource("result.".concat(imageType), () -> new ByteArrayInputStream(result)), "result");
        beforeImage.getStyle().set("width", "48vw");
        resultImage.getStyle().set("width", "48vw");

        Div images = new Div();
        images.add(beforeImage, resultImage);
        images.getStyle().set("display", "flex");
        images.getStyle().set("justify-content", "space-between");

        processResult.add(images);
    }

    private void reloadPage() {
        logger.info("Перерисовка страницы");

        removeAll();
        init();
    }

    private void updateProcessType(String processType) {
        this.processType = ProcessType.getType(processType);
    }

    private void editParams() {
        params.removeAll();

        if (processType.doesNotNeedParams()) return;

        logger.info("Выбор параметров обработки изображения, выбранный тип обработки: " + processType);

        Text formula;
        NumberField cField = new NumberField();
        cField.setPlaceholder("Введите значение c");

        if (processType.equals(ProcessType.LOGARITHM)) {
            formula = new Text("s = c * log(1 + r), где s - яркость на выходе, r - на входе, а c - положительная константа.");

            params.add(formula, cField);
        }
        if (processType.equals(ProcessType.POWER)) {
            formula = new Text("s = c * r ^ Y, где s - яркость на выходе, r - на входе, а c и Y - положительные константы.");

            NumberField yField = new NumberField();
            yField.setPlaceholder("Введите значение Y");

            Div paramFields = new Div();
            paramFields.add(cField, yField);
            paramFields.getStyle().set("display", "flex");
            paramFields.getStyle().set("justify-content", "space-evenly");

            params.add(formula, paramFields);
        }
    }

    private boolean hasInvalidParams() {
        return switch(processType) {
            case LOGARITHM -> ((NumberField) params.getChildren().toList().get(1)).getValue() == null ||
                    ((NumberField) params.getChildren().toList().get(1)).getValue() < 0;
            case POWER ->  {
                List<Component> paramFields = params.getChildren().toList().get(1).getChildren().toList();
                for (Component parameter : paramFields) {
                    NumberField paramField = (NumberField) parameter;
                    if (paramField.getValue() == null || paramField.getValue() < 0) yield true;
                }
                yield false;
            }
            default -> false;
        };
    }

    private List<Double> getParams() {
        ArrayList<Double> result = new ArrayList();
        switch(processType) {
            case LOGARITHM -> result.add(((NumberField) params.getChildren().toList().get(0)).getValue());
            case POWER ->  {
                List<Component> paramFields = params.getChildren().toList().get(1).getChildren().toList();
                for (Component parameter : paramFields)
                    result.add(((NumberField) parameter).getValue());
            }
            default -> new ArrayList<>();
        };
        return result;
    }
}
