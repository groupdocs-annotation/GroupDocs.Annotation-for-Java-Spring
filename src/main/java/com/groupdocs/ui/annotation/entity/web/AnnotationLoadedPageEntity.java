package com.groupdocs.ui.annotation.entity.web;

import com.groupdocs.ui.model.response.LoadedPageEntity;

import java.util.List;

public class AnnotationLoadedPageEntity extends LoadedPageEntity {
    private List<TextRowEntity> coordinates;

    public List<TextRowEntity> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<TextRowEntity> coordinates) {
        this.coordinates = coordinates;
    }
}
