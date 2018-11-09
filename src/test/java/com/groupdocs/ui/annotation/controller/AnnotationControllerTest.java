package com.groupdocs.ui.annotation.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringRunner.class)
@SpringBootTest

public class AnnotationControllerTest {
    MockMvc mvc;

    @Autowired
    protected WebApplicationContext wac;

    @Autowired
    AnnotationController controller;

    @Before
    public void setUp() throws Exception {
        this.mvc = standaloneSetup(this.controller).build();
    }

    @Test
    public void getView()  throws Exception {
        mvc.perform(get("/annotation").contentType(MediaType.APPLICATION_XHTML_XML))
                .andExpect(status().isOk());
    }

    public void loadFileTree() {
    }

    public void loadDocumentDescription() {
    }

    public void loadDocumentPage() {
    }

    public void downloadDocument() {
    }

    public void uploadDocument() {
    }

    public void textCoordinates() {
    }

    public void annotate() {
    }
}