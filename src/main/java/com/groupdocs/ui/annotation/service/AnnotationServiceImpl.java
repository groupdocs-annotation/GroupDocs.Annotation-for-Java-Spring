package com.groupdocs.ui.annotation.service;

import com.google.common.collect.Lists;
import com.groupdocs.annotation.common.exception.AnnotatorException;
import com.groupdocs.annotation.common.license.License;
import com.groupdocs.annotation.domain.AnnotationInfo;
import com.groupdocs.annotation.domain.FileDescription;
import com.groupdocs.annotation.domain.PageData;
import com.groupdocs.annotation.domain.config.AnnotationConfig;
import com.groupdocs.annotation.domain.containers.DocumentInfoContainer;
import com.groupdocs.annotation.domain.containers.FileTreeContainer;
import com.groupdocs.annotation.domain.image.PageImage;
import com.groupdocs.annotation.domain.options.FileTreeOptions;
import com.groupdocs.annotation.domain.options.ImageOptions;
import com.groupdocs.annotation.handler.AnnotationImageHandler;
import com.groupdocs.ui.annotation.annotator.AnnotatorFactory;
import com.groupdocs.ui.annotation.config.AnnotationConfiguration;
import com.groupdocs.ui.annotation.entity.request.AnnotateDocumentRequest;
import com.groupdocs.ui.annotation.entity.web.AnnotatedDocumentEntity;
import com.groupdocs.ui.annotation.entity.web.AnnotationDataEntity;
import com.groupdocs.ui.annotation.importer.Importer;
import com.groupdocs.ui.annotation.util.AnnotationMapper;
import com.groupdocs.ui.config.GlobalConfiguration;
import com.groupdocs.ui.exception.TotalGroupDocsException;
import com.groupdocs.ui.model.request.FileTreeRequest;
import com.groupdocs.ui.model.request.LoadDocumentPageRequest;
import com.groupdocs.ui.model.request.LoadDocumentRequest;
import com.groupdocs.ui.model.response.FileDescriptionEntity;
import com.groupdocs.ui.model.response.LoadedPageEntity;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static com.groupdocs.ui.annotation.util.DocumentTypesConverter.getDocumentType;
import static com.groupdocs.ui.annotation.util.PathConstants.OUTPUT_FOLDER;

@Service
public class AnnotationServiceImpl implements AnnotationService {
    private static final Logger logger = LoggerFactory.getLogger(AnnotationServiceImpl.class);

    private static final List<String> supportedImageFormats = Lists.newArrayList("bmp", "jpeg", "jpg", "tiff", "tif", "png", "gif", "emf", "wmf", "dwg", "dicom", "djvu");
    private static final List<String> supportedAutoCadFormats = Lists.newArrayList("dxf", "dwg");

    @Autowired
    private GlobalConfiguration globalConfiguration;

    @Autowired
    private AnnotationConfiguration annotationConfiguration;

    private AnnotationImageHandler annotationHandler;

    @PostConstruct
    public void init() {
        // init output directory
        initOutputDirectory();
        // create annotation application configuration
        AnnotationConfig config = new AnnotationConfig();
        // set storage path
        config.setStoragePath(annotationConfiguration.getFilesDirectory());
        config.getFontDirectories().add(annotationConfiguration.getFontsDirectory());

        annotationHandler = new AnnotationImageHandler(config);

        try {
            // set GroupDocs license
            License license = new License();
            license.setLicense(globalConfiguration.getApplication().getLicensePath());
        } catch (Throwable exc) {
            logger.error("Can not verify Annotation license!");
        }
    }

    private void initOutputDirectory() {
        if(StringUtils.isEmpty(annotationConfiguration.getOutputDirectory())) {
            String outputDirectory = String.format("%s%s", annotationConfiguration.getFilesDirectory(), OUTPUT_FOLDER);
            annotationConfiguration.setOutputDirectory(outputDirectory);
        }
        if(!new File(annotationConfiguration.getOutputDirectory()).exists()) {
            new File(annotationConfiguration.getOutputDirectory()).mkdirs();
        }
    }

    @Override
    public GlobalConfiguration getGlobalConfiguration() {
        return globalConfiguration;
    }

    @Override
    public AnnotationConfiguration getAnnotationConfiguration() {
        return annotationConfiguration;
    }

    @Override
    public List<FileDescriptionEntity> getFileList(FileTreeRequest fileTreeRequest) {
        String path = fileTreeRequest.getPath();
        // get file list from storage path
        FileTreeOptions fileListOptions = new FileTreeOptions(path);
        // get temp directory name
        String tempDirectoryName = new AnnotationConfig().getTempFolderName();
        try {
            FileTreeContainer fileListContainer = getAnnotationImageHandler().loadFileTree(fileListOptions);

            List<FileDescriptionEntity> fileList = new ArrayList<>();
            // parse files/folders list
            for (FileDescription fd : fileListContainer.getFileTree()) {
                FileDescriptionEntity fileDescription = new FileDescriptionEntity();
                fileDescription.setGuid(fd.getGuid());
                // check if current file/folder is temp directory or is hidden
                if (tempDirectoryName.toLowerCase().equals(fd.getName()) || new File(fileDescription.getGuid()).isHidden()) {
                    // ignore current file and skip to next one
                    continue;
                } else {
                    // set file/folder name
                    fileDescription.setName(fd.getName());
                }
                // set file type
                fileDescription.setDocType(fd.getDocumentType());
                // set is directory true/false
                fileDescription.setDirectory(fd.isDirectory());
                // set file size
                fileDescription.setSize(fd.getSize());
                // add object to array list
                fileList.add(fileDescription);
            }
            return fileList;
        } catch (Exception ex) {
            throw new TotalGroupDocsException(ex.getMessage(), ex);
        }
    }

    @Override
    public List<AnnotatedDocumentEntity> getDocumentDescription(LoadDocumentRequest loadDocumentRequest) {
        try {
            // get/set parameters
            String documentGuid = loadDocumentRequest.getGuid();
            String password = loadDocumentRequest.getPassword();
            // get document info container
            String fileName = new File(documentGuid).getName();
            DocumentInfoContainer documentDescription = getAnnotationImageHandler().getDocumentInfo(fileName, password);

            String documentType = documentDescription.getDocumentType();
            String fileExtension = parseFileExtension(documentGuid);
            // check if document type is image
            if (supportedImageFormats.contains(fileExtension)) {
                documentType = "image";
            } else if (supportedAutoCadFormats.contains(fileExtension)) {
                documentType = "diagram";
            }
            // check if document contains annotations
            AnnotationInfo[] annotations = getAnnotations(documentGuid, documentType);
            // initiate pages description list
            List<AnnotatedDocumentEntity> pagesDescription = new ArrayList<>();
            // get info about each document page
            List<PageData> pages = documentDescription.getPages();
            for (int i = 0; i < pages.size(); i++) {
                // initiate custom Document description object
                AnnotatedDocumentEntity description = new AnnotatedDocumentEntity();
                description.setGuid(documentGuid);
                // set current page info for result
                PageData pageData = pages.get(i);
                description.setHeight(pageData.getHeight());
                description.setWidth(pageData.getWidth());
                description.setNumber(pageData.getNumber());
                // set annotations data if document page contains annotations
                if (annotations != null && annotations.length > 0) {
                    description.setAnnotations(AnnotationMapper.instance.mapForPage(annotations, description.getNumber()));
                }
                pagesDescription.add(description);
            }
            // return document description
            return pagesDescription;
        } catch (Exception ex) {
            throw new TotalGroupDocsException(ex.getMessage(), ex);
        }
    }

    @Override
    public LoadedPageEntity getDocumentPage(LoadDocumentPageRequest loadDocumentPageRequest) {
        try {
            // get/set parameters
            String documentGuid = loadDocumentPageRequest.getGuid();
            int pageNumber = loadDocumentPageRequest.getPage();
            String password = loadDocumentPageRequest.getPassword();
            // set options
            ImageOptions imageOptions = new ImageOptions();
            imageOptions.setPageNumber(pageNumber);
            imageOptions.setCountPagesToConvert(1);
            // set password for protected document
            if (!password.isEmpty()) {
                imageOptions.setPassword(password);
            }
            // get page image
            InputStream document = new FileInputStream(documentGuid);
            List<PageImage> images = getAnnotationImageHandler().getPages(document, imageOptions);

            byte[] bytes = IOUtils.toByteArray(images.get(pageNumber - 1).getStream());
            // encode ByteArray into String
            String encodedImage = Base64.getEncoder().encodeToString(bytes);

            // loaded page object
            LoadedPageEntity loadedPage = new LoadedPageEntity();
            loadedPage.setPageImage(encodedImage);
            return loadedPage;
        } catch (Exception ex) {
            throw new TotalGroupDocsException(ex.getMessage(), ex);
        }
    }

    /**
     * Create new instance for AnnotationImageHandler
     *
     * @return AnnotationImageHandler
     */
    private AnnotationImageHandler getAnnotationImageHandler() {
        return annotationHandler;
    }

    @Override
    public AnnotatedDocumentEntity annotate(AnnotateDocumentRequest annotateDocumentRequest) {
        AnnotatedDocumentEntity annotatedDocument = new AnnotatedDocumentEntity();
        try {
            // get/set parameters
            String documentGuid = annotateDocumentRequest.getGuid();
            String password = annotateDocumentRequest.getPassword();
            AnnotationDataEntity[] annotationsData = annotateDocumentRequest.getAnnotationsData();
            String documentType = annotateDocumentRequest.getDocumentType();
            // initiate AnnotatedDocument object
            // get document info - required to get document page height and calculate annotation top position
            DocumentInfoContainer documentInfo = getAnnotationImageHandler().getDocumentInfo(new File(documentGuid).getName(), password);
            // check if document type is image
            String fileExtension = parseFileExtension(documentGuid);
            if (supportedImageFormats.contains(fileExtension)) {
                documentType = "image";
            }
            // initiate list of annotations to add
            List<AnnotationInfo> annotations = new ArrayList<>();
            Throwable exc = null;
            for (int i = 0; i < annotationsData.length; i++) {
                // create annotator
                AnnotationDataEntity annotationData = annotationsData[i];
                PageData pageData = documentInfo.getPages().get(annotationData.getPageNumber() - 1);
                // add annotation, if current annotation type isn't supported by the current document type it will be ignored
                try {
                    annotations.add(AnnotatorFactory.createAnnotator(annotationData, pageData).getAnnotationInfo(documentType));
                } catch (UnsupportedOperationException ex) {
                    exc = ex;
                } catch (Exception ex) {
                    throw new TotalGroupDocsException(ex.getMessage(), ex);
                }
            }
            // check if annotations array contains at least one annotation to add
            if (annotations.size() > 0) {
                // Add annotation to the document
                int type = getDocumentType(documentType);
                // Save result stream to file.
                String fileName = new File(documentGuid).getName();
                String path = annotationConfiguration.getOutputDirectory() + File.separator + fileName;
                try (InputStream cleanDoc = new FileInputStream(documentGuid);
                     InputStream result = getAnnotationImageHandler().exportAnnotationsToDocument(cleanDoc, annotations, type);
                     OutputStream fileStream = new FileOutputStream(path)) {

                    IOUtils.copyLarge(result, fileStream);
                }
                annotatedDocument.setGuid(path);
            } else if (exc != null) {
                throw new UnsupportedOperationException(exc.getMessage(), exc);
            }
        } catch (Exception ex) {
            throw new TotalGroupDocsException(ex.getMessage(), ex);
        }
        return annotatedDocument;
    }

    public String parseFileExtension(String documentGuid) {
        String extension = FilenameUtils.getExtension(documentGuid);
        return extension == null ? null : extension.toLowerCase();
    }

    /**
     * Get all annotations from the document
     *
     * @param documentGuid
     * @param documentType
     * @return array of the annotations
     */
    private AnnotationInfo[] getAnnotations(String documentGuid, String documentType) {
        try (InputStream documentStream = new FileInputStream(documentGuid)) {
            AnnotationImageHandler annotationImageHandler = getAnnotationImageHandler();
            int docType = getDocumentType(documentType);
            return new Importer(documentStream, annotationImageHandler).importAnnotations(docType);
        } catch (AnnotatorException annotatorException) {
            logger.error("Exception while extract annotations from file {}: {}", FilenameUtils.getName(documentGuid), annotatorException.getCause().getLocalizedMessage());
            return new AnnotationInfo[0];
        } catch (Exception ex) {
            throw new TotalGroupDocsException(ex.getMessage(), ex);
        }
    }

}
