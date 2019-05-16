# Getting Started

Welcome to the bookshop-java project. The following sections describe how to setup, build and deploy the project on a local Tomcat server. It demonstrates a recommended project layout for CAP applications.

## Prerequisites 
1.  Install **Java 8** (<https://tools.hana.ondemand.com/#cloud>) and set JAVA_HOME. 
2.  Install **Apache Maven** (<https://maven.apache.org/download.cgi>) and set M2_HOME
3.  Configure **Maven** for the SAP build landscape by downloading [settings.xml](http://nexus.wdf.sap.corp:8081/nexus/service/local/templates/settings/LeanDI/content) and save it to **<USER_HOME>/.m2/settings.xml**.
4.  Install **Nodejs V8** and set the sap npm registry using the command: 
 ```
    npm set @sap:registry=https://npm.sap.com
 ```
5.  Install **Eclipse**. Check the link: <https://github.wdf.sap.corp/cdx/cds-ls4e/wiki/Installation> for compatibility issues.
6.  Install **SAP Cloud platform tools** in eclipse from the url: <https://tools.hana.ondemand.com/oxygen>
7.  Install **SAP Cloud Business Application Tools** and  **Multitarget Application Archive Builder** in eclipse from the url: <http://cdstools.mo.sap.corp:1080/sites/eclipse/release/>

## Setting up the Tomcat server
1.  Download **Java Web Tomcat 8** from <https://tools.hana.ondemand.com/#cloud>
2.  Open eclipse and goto **File>New>Other**. Select **Server** and click **Next** at the bottom of the window.
3.  Select server type as **Java Web Tomcat 8 Server** under **SAP**. Click Next.
4.  Browse to the path where the tomcat sdk is located. Click Next.
5.  Click Finish.

## Setting up the project using Git
1.  Clone the project into the desired location using the command below: 
```
  git clone https://github.wdf.sap.corp/caps/bookshop-java.git
```
2.  Import the project as **SAP Cloud Business Application**. Give the path to where **mta.yaml** is located. Also tick the checkbox: Import service modules as Maven Projects. 
Now you see **bookshop-java** and **bookshop-java-srv** in the project/package explorer view.
3.  Change the Projects Presentation from Flat to Hierarchical for better understanding.
4.  Right click on the project, then go to the SAP Cloud Business Application and click Build.
5.  Right click on the bookshop-java-srv project and Run on server (**Java Web Tomcat 8 Server**).
6.  Use the link <http://localhost:8080/bookshop-java-srv/odata/v4/> or  
 <http://localhost:8080/bookshop-java-srv/odata/v4/CatalogService/$metadata> in the browser to check if everything works fine.

## Setting up the **db** module
1.  Create a service instance named **bookshop-java-hdi-container** using the command below:

```
   on cloudfoundry: cf create-service hana hdi-shared bookshop-java-hdi-container
   on XSA: xs create-service hana hdi-shared bookshop-java-hdi-container
```
2.  Create service key named **bookshop-java-hdi-container-key** for the service instance **bookshop-java-hdi-container** using the command below:

```
   on cloudfoundry: cf create-service-key bookshop-java-hdi-container bookshop-java-hdi-container-key
   on XSA: xs create-service-key bookshop-java-hdi-container bookshop-java-hdi-container-key
```
3.  Get the value of the attribute **VCAP_SERVICES** for the **bookshop-java-db application** and its value for the for using the command:

```
   on Cloudfoundry: cf env bookshop-java-db
   on XSA: xs env bookshop-java-db
   ```
4.  Replace the value of the attribute **VCAP_SERVICES**  in **default-env.json** under the **db** module.
5.  Right click on the **db** module and click **deploy** on the **SAP HANA database module** option. 
The application connects to the container, creates the tables and puts the data from the csv files into the tables. 
The mapping between csv and the table columns is present in the **Data.hdbtabledata** file.

## Setting up the **srv** module
1.  Update the **connection.properties** file under **/bookshop-java-srv/src/main/resources/**
with the corresponding values of **VCAP_SERVICES** (see **step 2** under setting up **db** module)
2.  Right click on the **bookshop-java-srv**(service module) and click on **build** under **Service module** option.
This generates the corresponding **csn.json** and service xml files under the **edmx** folder in the **srv** folder and, generates the corresponding database scripts to create the tables and the views.

## Setting up the app module
1.  Open **Servers** view. Servers View can be found under **Window>Show View>Servers**.
2.  Delete all the deployed applications deployed on **Java Web Tomcat8 Server**, if any.
3.  Double click on the **Java Web Tomcat8 Server** to open the Overview.
4.  Under **Server Locations** drop-down, select **Use custom location**. Provide the **Server path**. It might be necessary to copy your server directory because eclipse does not allow to use the same directory to add the server and use it as custom location.
5.  Create a file, **rewrite.config** inside **/Path-To-Tomcat/neo-java-web-sdk-3.75.12/conf/Catalina/localhost**
6.  Add the lines below in the **rewrite.config** file: 

```
   RewriteCond %{REQUEST_URI} !^(.*)/webapp(.*)$ 
   RewriteRule  ^/admin/(.*)$  /bookshop-java-srv/odata/v4/AdminService/$1
   RewriteRule  ^/catalog/(.*)$  /bookshop-java-srv/odata/v4/CatalogService/$1
```

7.  In the Project explorer open the server **server.xml** inside **Servers>Java Web Tomcat8 Server-config** and make the following changes:

   ```diff
     <Engine defaultHost="localhost" name="Catalina">

      <Host appBase="webapps" autoDeploy="true" name="localhost" unpackWARs="true">
+       <Valve className="org.apache.catalina.valves.rewrite.RewriteValve"/>
        <!-- SingleSignOn valve, share authentication between web applications
             Documentation at: /docs/config/valve.html -->
        <!--
        <Valve className="org.apache.catalina.authenticator.SingleSignOn" />
        -->
        
        <Valve className="org.apache.catalina.valves.ErrorReportValve" showServerInfo="false"/>
        <Valve className="com.sap.core.js.monitoring.tomcat.valve.RequestTracingValve"/>
        <Valve className="com.sap.js.statistics.tomcat.valve.RequestTracingValve"/>
        <Valve className="com.sap.cloud.runtime.impl.bridge.tenant.TenantValveWrapper"/>
        <!-- Access log processes all example.
             Documentation at: /docs/config/valve.html
             Note: The pattern used is equivalent to using pattern="common" -->
        <Valve className="org.apache.catalina.valves.AccessLogValve" directory="log" pattern="%h %l %u %t &quot;%r&quot; %s %b" prefix="localhost_access_log." suffix=".txt"/>
        
        <!-- 
           Make sure that the <Context> order is same as below.
        -->
+       <Context docBase="/home/D070324/work/CAP/git_projects/bookshop-java/node_modules/@sap/capm-samples-bookshop/app/" path=""/>
        <Context docBase="bookshop-java-srv" path="/bookshop-java-srv" reloadable="true" source="org.eclipse.jst.jee.server:bookshop-java-srv"/></Host>
  
    </Engine>
```
5.  Right click on the service module(**bookshop-java-srv**) inside eclipse and run on Server.
6.  Open the browser and put the url <http://localhost:8080/> in the address bar. The UI is now visible.

## Futher Reading
1. [capire](https://github.wdf.sap.corp/pages/cap/)
2. [cdx/cds-ls4e](https://github.wdf.sap.corp/cdx/cds-ls4e/wiki)
3. [SAP Cloud Platform Documentation](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/00823f91779d4d42aa29a498e0535cdf.html) 
4. [SAP HANA Core Data Services (CDS) Reference](https://help.sap.com/viewer/09b6623836854766b682356393c6c416/2.0.02/en-US)

