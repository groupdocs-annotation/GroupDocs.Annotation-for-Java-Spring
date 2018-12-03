package com.groupdocs.ui.annotation.entity.web;

import com.groupdocs.ui.model.request.LoadDocumentRequest;
import com.groupdocs.ui.model.response.DocumentDescriptionEntity;

/**
 * AnnotatedDocumentEntity
 *
 * @author Aspose Pty Ltd
 */
public class AnnotatedDocumentEntity extends LoadDocumentRequest {
    /**
     * List of annotation data
     */
    private AnnotationDataEntity[] annotations;

    // TODO: remove once perf. issue is fixed
    private String data;

    private double width;
    private double height;
    private int number;


    public AnnotationDataEntity[] getAnnotations() {
        return annotations;
    }

    public void setAnnotations(AnnotationDataEntity[] annotations) {
        this.annotations = annotations;
    }

    public String getData(){return data;}

    public void setData(String image){ this.data = image;}

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

}
