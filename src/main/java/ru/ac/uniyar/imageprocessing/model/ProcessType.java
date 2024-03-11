package ru.ac.uniyar.imageprocessing.model;

import lombok.Getter;


@Getter
public enum ProcessType {


    HALFTONE("Преобразование цветного изображения в полутоновое"),
    BINARY("Преобразование полутонового изображения в бинарное (черно-белое)"),
    NEGATIVE("Преобразование изображения в негатив"),
    LOGARITHM("Логарифмическое преобразование"),
    POWER("Степенное преобразование");

    private final String name;
    ProcessType(String name) {
        this.name = name;
    }

    public static ProcessType getType(String processType) {
        for (ProcessType type : ProcessType.values())
            if (processType.equals(type.getName()))
                return type;
        return null;
    }

    public boolean doesNotNeedParams() {
        return !this.name.equals(LOGARITHM.getName()) && !this.name.equals(POWER.getName());
    }
}
