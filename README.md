# Getting Started

Welcome to the bookshop-java project. It demonstrates a simple application built on CAP Java.
The following sections describe how to setup, build and run the project.

## Prerequisites 
Make sure you have setup a development environment [as described here](https://github.wdf.sap.corp/pages/cap/java/overview#javalocaldev).

## Importing the Project in Eclipse
1.  Clone the project:
```
  git clone https://github.wdf.sap.corp/caps/bookshop-java.git
```
2.  Import the project using **File > Import > Existing Maven Projects**. 
    
    Now you should see the projects **bookshop** and **bookshop-parent** in the project/package explorer view.
3.  In Project Explorer, change the property "Package Presentation" from "Flat" to "Hierarchical" for better understanding.

## Building and running
1.  To **compile** the project, right-click on the file `pom.xml` in the `bookshop-parent` project root folder and select 
**Run as > Maven build**. 

    In the following dialog, enter the string `clean install` into the field labeled with "Goals" and click on "Run".

    Note: This step also compiles the CDS artifacts, thus repeat this once you made changes to the CDS model. This step also generates source files, therefore refresh the "bookshop" project in your IDE.

2.  To **run** the application, right-click on the `bookshop` project root in the Package Explorer and select **Run as > Spring Boot App** (make sure you have [Spring Tools 4 installed](https://github.wdf.sap.corp/cds-java/cds-services/blob/master/docs/local_dev_env.md)).

    This step creates a default Run Configuration named `Bookshop - Application` and starts the application afterwards. To go on with the next step, stop the application again.

3.  Then, set the default working directory by editing your Run Configuration via **Run -> Run Configurations -> Bookshop - Application**. On the tab **Arguments** change the default **Working Directory** to

	```${workspace_loc:bookshop-parent}```

	Afterwards, click on **Run**. This step tarts the applications `main` method located in `src/main/java/my/bookshop/Application.java`.
    
3.  Use the following links in the browser to check if everything works fine:

    <http://localhost:8080/>: This should show the automatically generated index page of served paths.  
    <http://localhost:8080/fiori.html>: This is the actual bookshop application UI

    You'll start with an empty stock of books as this procedure starts the bookshop application with an empty in-memory sqlite database. Two mock users are defined for local development:

    - User: `user`, password: `user` to browse books
    - User: `admin`, password: `admin` to manage books and orders

## Database setup and Spring profiles
The application comes with three predefined profiles: `default`, `sqlite` and `cloud` (see `src/main/resources/application.yaml`)

- The `default` profile specifies to use an in-memory SQLite database. 
  The in-memory database is setup automatically during startup of the application. 
  However example data from CSV files are not yet automatically imported, therefore some content needs to be created via OData V4 API requests.

- The `sqlite` profile specifies to use a persistent SQLite database from root directory of the project.
  This database needs to be created first, to ensure it is initialized with the correct schema and with the CSV-based example data.
  To initialize the database, simply run `cds deploy` from the project's root directory. Repeat this step, once you make changes to the CDS model.

- When deploying the application to the CloudFoundry, the CF Java Buildpack automatically configures the `cloud` Spring profile.
  This profile does not specify any datasource location. In that case CAP Java can automatically detect HANA service bindings available in the environment.

## Using a file-based SQLite database

To switch from the default in-memory SQLite database to a file-based SQLite database in this sample application perform the following steps:

1.  Deploy the example data stored in .csv files in the folder ``db/data`` to a file-based SQLite database by executing the command-line utility

    ```cds deploy```

    from your project root folder.

2.  Edit your Run Configuration via **Run -> Run Configurations...** and select enter the **Profile** `sqlite` on tab **Spring** and click on **Run**.

## Demonstrated features
- Application configuration
- How to add custom event handlers
- Authentication & Authorization
- Mocking users for local development
- Localization
- Fiori support & Fiori Drafts

## Futher Reading
- [capire](https://github.wdf.sap.corp/pages/cap/)
- [cdx/cds-ls4e](https://github.wdf.sap.corp/cdx/cds-ls4e/wiki)

