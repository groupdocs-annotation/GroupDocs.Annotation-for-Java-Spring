![Alt text](https://raw.githubusercontent.com/groupdocs-annotation/groupdocs-annotation.github.io/master/resources/image/banner.png "GroupDocs.Annotation")
# GroupDocs.Annotation-for-Java-Spring Example
###### version 1.12.13

[![Build Status](https://travis-ci.org/groupdocs-annotation/GroupDocs.Annotation-for-Java-Spring.svg?branch=master)](https://travis-ci.org/groupdocs-annotation/GroupDocs.Annotation-for-Java-Spring)
[![Maintainability](https://api.codeclimate.com/v1/badges/f6de7b4597a02ddb09df/maintainability)](https://codeclimate.com/github/groupdocs-annotation/GroupDocs.Annotation-for-Java-Spring/maintainability)

## System Requirements
- Java 8 (JDK 1.8)
- Maven 3


## Description
You asked, we delivered! Welcome our new and improved GroupDocs.Annotation UI - native, simple, fully configurable and optimized Java document Annotation application.

**Note** Without a license application will run in trial mode, purchase [GroupDocs.Annotation for Java license](https://purchase.groupdocs.com/order-online-step-1-of-8.aspx) or request [GroupDocs.Annotation for Java temporary license](https://purchase.groupdocs.com/temporary-license).


## Demo Video
https://www.youtube.com/watch?v=TVb7GwSNDqw


## Features
- Clean, modern and intuitive design
- Easily switchable colour theme (create your own colour theme in 5 minutes)
- Responsive design
- Mobile support (open application on any mobile device)
- Support over 50 documents and image formats
- Image mode
- Fully customizable navigation panel
- Annotate password protected documents
- Download original documents
- Download annotated documents
- Upload documents
- Annotate document with such annotation types: 
   * Text
   * Area
   * Point
   * TextStrikeout
   * Polyline
   * TextField
   * Watermark
   * TextReplacement
   * Arrow
   * TextRedaction
   * ResourcesRedaction
   * TextUnderline
   * Distance
- Draw annotation over the document page
- Add comment or reply
- Print document
- Smooth page navigation
- Smooth document scrolling
- Preload pages for faster document rendering
- Multi-language support for displaying errors
- Cross-browser support (Safari, Chrome, Opera, Firefox)
- Cross-platform support (Windows, Linux, MacOS)

## How to run

You can run this sample by one of following methods

#### Build from source

Download [source code](https://github.com/groupdocs-annotation/GroupDocs.Annotation-for-Java-Spring/archive/master.zip) from github or clone this repository.

```bash
git clone https://github.com/groupdocs-annotation/GroupDocs.Annotation-for-Java-Spring
cd GroupDocs.Annotation-for-Java-Spring
mvn clean spring-boot:run
## Open http://localhost:8080/annotation/ in your favorite browser.
```

#### Build war from source

Download [source code](https://github.com/groupdocs-annotation/GroupDocs.Annotation-for-Java-Spring/archive/master.zip) from github or clone this repository.

```bash
git clone https://github.com/groupdocs-annotation/GroupDocs.Annotation-for-Java-Spring
cd GroupDocs.Annotation-for-Java-Spring
mvn package -P war
## Deploy this war on any server
```

#### Binary release (with all dependencies)

Download [latest release](https://github.com/groupdocs-annotation/GroupDocs.Annotation-for-Java-Spring/releases/latest) from [releases page](https://github.com/groupdocs-annotation/GroupDocs.Annotation-for-Java-Spring/releases). 

**Note**: This method is **recommended** for running this sample behind firewall.

```bash
curl -J -L -o release.tar.gz https://github.com/groupdocs-annotation/GroupDocs.Annotation-for-Java-Spring/releases/download/1.12.13/release.tar.gz
tar -xvzf release.tar.gz
cd release
java -jar annotation-spring-1.12.13.jar configuration.yml
## Open http://localhost:8080/annotation/ in your favorite browser.
```

#### Docker image
Use [docker](https://www.docker.com/) image.

```bash
mkdir DocumentSamples
mkdir Licenses
docker run -p 8080:8080 --env application.hostAddress=localhost -v `pwd`/DocumentSamples:/home/groupdocs/app/DocumentSamples -v `pwd`/Licenses:/home/groupdocs/app/Licenses groupdocs/annotation
## Open http://localhost:8080/annotation/ in your favorite browser.
```

#### Configuration
For all methods above you can adjust settings in `configuration.yml`. By default in this sample will lookup for license file in `./Licenses` folder, so you can simply put your license file in that folder or specify relative/absolute path by setting `licensePath` value in `configuration.yml`. 


## Resources
- **Website:** [www.groupdocs.com](http://www.groupdocs.com)
- **Product Home:** [GroupDocs.Annotation for Java](https://products.groupdocs.com/annotation/java)
- **Product API References:** [GroupDocs.Annotation for Java API](https://apireference.groupdocs.com)
- **Download:** [Download GroupDocs.Annotation for Java](http://downloads.groupdocs.com/annotation/java)
- **Documentation:** [GroupDocs.Annotation for Java Documentation](https://docs.groupdocs.com/dashboard.action)
- **Free Support Forum:** [GroupDocs.Annotation for Java Free Support Forum](https://forum.groupdocs.com/c/annotation)
- **Paid Support Helpdesk:** [GroupDocs.Annotation for Java Paid Support Helpdesk](https://helpdesk.groupdocs.com)
- **Blog:** [GroupDocs.Annotation for Java Blog](https://blog.groupdocs.com/category/groupdocs-annotation-product-family)
