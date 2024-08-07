package com.salesphere.salesphere.models.enums;

public enum CategoryEnum {
    MALE("Masculino"),
    FEMALE("Feminino"),
    SHOES("Calçados"),
    CHILDREN("Infantil"),
    ACCESSORIES("Acessórios");

    private final String description;

    CategoryEnum(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}