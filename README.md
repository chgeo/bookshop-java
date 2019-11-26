# Getting Started

Welcome to the bookshop-java project. It demonstrates a simple application built on CAP Java.
The following sections describe how to setup, build and run the project.

## Prerequisites 
Make sure you have setup a development environment [as described here](https://github.wdf.sap.corp/pages/cap/java/overview#javalocaldev).

## Importing the Project in Eclipse
1.  Clone the project into the desired location using the command below: 
```
  git clone https://github.wdf.sap.corp/caps/bookshop-java.git
```
2.  Import the project using **File > Import > Existing Maven Projects**.  
Now you see **bookshop** and **bookshop-parent** in the project/package explorer view.
3.  Change the Projects Presentation from Flat to Hierarchical for better understanding.

## Building and running
1.  To **compile** the project, right-click on the file `pom.xml` in the `bookshop-parent` project root folder and select 
**Run as > Maven build**.  
    In the following dialog, enter the string `clean install` to the field labeled with "Goals" and click on "Run".  
    This step also compiles the CDS artifacts, repeat this once you make changes to the CDS model.
2.  To **run** the application, right-click on the `bookshop` project root in the Package Explorer and select **Run as > Spring Boot App** (make sure you have [Spring Tools 4 installed](https://github.wdf.sap.corp/cds-java/cds-services/blob/master/docs/local_dev_env.md)).
3.  Edit the created Spring Boot run configuration
    - select `sqlite` as profile in tab **Spring Boot**
    - select the bookshop-parent project as working directory in the **Arguments** tab:  
      Working directory > Other: "${workspace_loc:bookshop-parent}"
    
4.  Use the following links in the browser to check if everything works fine:  
  <http://localhost:8080/>:  This should show the automatically generated index page of served paths.
  <http://localhost:8080/fiori.html>:  This is the actual bookshop application UI

## Database setup and Spring profiles
The application comes with three predefined profiles: `default`, `sqlite` and `cloud`.

The `default` profiles specifies to use an in-memory SQLite database. 
The in-memory database is setup automatically during startup of the application. 
However example data from CSV files are not yet automatically imported, therefore some content needs to be created via OData V4 API requests.

The `sqlite` profile specifies to use a persistent SQLite database from root directory of the project.
This database needs to be created first, to ensure it is initialized with the correct schema and with the CSV-based example data.
To initialize the database, simply run `npm run deploy` from the project's root directory. Repeat this step, once you make changes to the CDS model.

When deploying the application to the CloudFoundry, the CF Java Buildpack automatically configures the `cloud` Spring profile.
This profile does not specify any datasource location. In that case CAP Java can automatically detect HANA service bindings available in the environment.

## Demonstrated features
- Security: Authentication and Authorization
- Localization
- Fiori Support & Drafts
- Custom Event Handlers
- ...

## Futher Reading
- [capire](https://github.wdf.sap.corp/pages/cap/)
- [cdx/cds-ls4e](https://github.wdf.sap.corp/cdx/cds-ls4e/wiki)

