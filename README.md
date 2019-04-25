# Serving UI with tomcat
You can serve the static content for the UI with the same tomcat server you are using to run the odata service. To do this no project changes are necessary. You just need to configure your local tomcat server.

1. Make sure you have selected "Use custom location" as server location in the eclipse server settings. You access these settings by double-clicking on the server in the "server" view. If you already deployed your app to the server, just remove it again to be able to change the location. As location choose the server location on the file system.
2. Via the "projects" view go to the "Servers" project and open the server.xml. Then add the following lines
```diff
    <Engine defaultHost="localhost" name="Catalina">

      <Host appBase="webapps" autoDeploy="true" name="localhost" unpackWARs="true">
+       <Valve className="org.apache.catalina.valves.rewrite.RewriteValve" />

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
      
+     <Context docBase="/Users/d064456/git/bookshop-java/node_modules/@sap/capm-samples-bookshop/app/" path=""/>        
      <Context docBase="bookshop-java-srv" path="/bookshop-java-srv" reloadable="true" source="org.eclipse.jst.jee.server:bookshop-java-srv"/></Host>
    </Engine>
  </Service>
</Server>
``` 
Note that the order of the ```Context``` lines is important. Also adapt the path so that it points to your project. The new ```Context``` configures the tomcat to serve the UI. The ```Valve``` is used to rewrite certain URLs that are requested by the UI.

3. On the file system go to the directory of your tomcat server. Then go to ```conf/Catalina/localhost/``` and create a new    file ```rewrite.config```. Add the following content to the file and save it:
   ```
   RewriteCond %{REQUEST_URI} !^(.*)/webapp(.*)$
   RewriteRule  ^/admin/(.*)$  /bookshop-java-srv/odata/v4/AdminService/$1
   RewriteRule  ^/catalog/(.*)$  /bookshop-java-srv/odata/v4/CatalogService/$1
   ```

   Now start the server and open http://localhost:8080. The rewrite rules are necessary because the UI is expecting the odata endpoints under the paths [/admin/](/admin/) and [/catalog/](/catalog/)

# Getting Started

Welcome to your new project. It contains a few files and folders following our **recommended project layout**:


File / Folder | Purpose
---------|----------
`srv/` | your models and code go here
`db/` | your database content goes here
`package.json` | project metadata and configuration
`readme.md` | this readme


The following sections are a quick walkthrough of essential tasks:

<!-- TOC depthFrom:2 depthTo:2 -->

- [Define your first Service and Run it](#define-your-first-service-and-run-it)
- [Add Data Models](#add-data-models)
- [Add Databases](#add-databases)
- [Add Initial Data](#add-initial-data)
- [Add Custom Logic](#add-custom-logic)
- [More...](#more)

<!-- /TOC -->

## Define your first Service and Run it

Create a file [srv/cat-service.cds](srv/cat-service.cds) and define a service in there as follows:

```swift
using { Country, managed } from '@sap/cds/common';

service CatalogService {

  entity Books {
    key ID : Integer;
    title  : localized String;
    author : Association to Authors;
    stock  : Integer;
  }

  entity Authors {
    key ID : Integer;
    name   : String;
    books  : Association to many Books on books.author = $self;
  }

  entity Orders : managed {
    key ID  : UUID;
    book    : Association to Books;
    country : Country;
    amount  : Integer;
  }
}
```


**[Run that](command:cds.run)** in a terminal to start a generic server:
```sh
> cds run
```

_Cmd/Ctrl-click_ on the link _<http://localhost:4004>_ in the output to open a browser and send requests to your service using the provided links.



## Add Data Models

Above we used a simplistic 'all-in-one' service definition for a quick start. Usually, you would put your entity definitions into a separate data model and have your **services expose views** on that entities.

Create a file named [db/data-model.cds](db/data-model.cds) and fill it with that:

```swift
namespace my.bookshop;
using { Country, managed } from '@sap/cds/common';

entity Books {
  key ID : Integer;
  title  : localized String;
  author : Association to Authors;
  stock  : Integer;
}

entity Authors {
  key ID : Integer;
  name   : String;
  books  : Association to many Books on books.author = $self;
}

entity Orders : managed {
  key ID  : UUID;
  book    : Association to Books;
  country : Country;
  amount  : Integer;
}
```

With that we can adapt our [srv/cat-service.cds](srv/cat-service.cds) to exposing views as follows:

```swift
using my.bookshop as my from '../db/data-model';

service CatalogService {
  entity Books    @readonly as projection on my.Books;
  entity Authors  @readonly as projection on my.Authors;
  entity Orders @insertonly as projection on my.Orders;
}
```


## Add Databases

The `cds` runtime ships with built-in generic handlers which automatically serve all CRUD requests SQL databases. We choose _sqlite_ for dev usage as that is already available on Macs ([&rarr; install it on Windows](https://www.sqlite.org/download.html)):

```bash
npm i sqlite3 -D
```

Now we can deploy our data model to a sqlite database:

```bash
cds deploy --to sqlite:my.db
```

This creates a sqlite database file at _[my.db](my.db)_. In addition, the given configuration is stored to your _package.json_ as your default data source. With that, you can run `cds deploy` subsquently without any arguments.



## Add Initial Data

Add plain CSV files under `db/csv` to fill your database tables with initial data.

1. Add a filed called [db/csv/my.bookshop-Authors.csv](db/csv/my.bookshop-Authors.csv) and add the following data:
```csv
ID;name
101;Emily Brontë
107;Charlote Brontë
150;Edgar Allen Poe
170;Richard Carpenter
```

2. Add a file called [db/csv/my.bookshop-Books.csv](db/csv/my.bookshop-Books.csv) and add the following data
```csv
ID;title;author_ID;stock
201;Wuthering Heights;101;12
207;Jane Eyre;107;11
251;The Raven;150;333
252;Eleonora;150;555
271;Catweazle;170;22
```

Run [`cds deploy`](command:cds.deploy)  again to have the data filled in.

Then [`cds run`](command:cds.run) the server again and see the data returned.

As we have now a fully cabable SQL database connected, we can leverage the querying capabilities of the generic handlers with requests like that: [.../Authors?$expand=books($select=ID,title)](http://localhost:4004/catalog/Authors?$expand=books($select=ID,title))



## Add Custom Logic
( Example for Node.js, skip for Java )

So far, all requests were served automatically by built-in generic service providers.
You can hook in to these providers to add your domain-specific logic. Just add an equally named `.js` file next to your service definition, i.e. [srv/cat-service.js](srv/cat-service.js), and fill this in:

```js
module.exports = (srv) => {

  const {Books} = cds.entities ('my.bookshop')

  // Reduce stock of ordered books
  srv.before ('CREATE', 'Orders', async (req) => {
    const order = req.data
    if (!order.amount || order.amount <= 0)  return req.error (400, 'Order at least 1 book')
    const tx = cds.transaction(req)
    const affectedRows = await tx.run (
      UPDATE (Books)
        .set   ({ stock: {'-=': order.amount}})
        .where ({ stock: {'>=': order.amount},/*and*/ ID: order.book_ID})
    )
    if (affectedRows === 0)  req.error (409, "Sold out, sorry")
  })

  // Add some discount for overstocked books
  srv.after ('READ', 'Books', each => {
    if (each.stock > 111)  each.title += ' -- 11% discount!'
  })

}
```

- Just open [.../Books](http://localhost:4004/catalog/Books) in the browser to test the handler for `Books`.

- For the `Orders` handler, you can use this [curl](https://curl.haxx.se/dlwiz/?type=bin) command (or use [Postman](https://www.getpostman.com/) with the data from below):
  ```sh
  curl http://localhost:4004/catalog/Orders -X POST -H "Content-Type: application/json" -d '{"book_ID":201, "amount":6}'
  ```

  After a few more requests the `Sold out` response is returned since the stock would drop below `0`.

## More...

Find more in the [help.sap.com](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/00823f91779d4d42aa29a498e0535cdf.html)
