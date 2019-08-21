
CREATE TABLE AdminService_OrderItems_drafts (
  ID NVARCHAR(36) NOT NULL,
  amount INTEGER NULL,
  netAmount DECIMAL(9, 2) NULL,
  parent_ID NVARCHAR(36) NULL,
  book_ID INTEGER NULL,
  IsActiveEntity BOOLEAN,
  HasActiveEntity BOOLEAN,
  HasDraftEntity BOOLEAN,
  DraftAdministrativeData_DraftUUID NVARCHAR(36) NOT NULL,
  PRIMARY KEY(ID)
);

CREATE TABLE AdminService_Orders_drafts (
  ID NVARCHAR(36) NOT NULL,
  modifiedAt TIMESTAMP NULL,
  createdAt TIMESTAMP NULL,
  createdBy NVARCHAR(255) NULL,
  modifiedBy NVARCHAR(255) NULL,
  OrderNo NVARCHAR(5000) NULL,
  total DECIMAL(9, 2) NULL,
  currency_code NVARCHAR(3) NULL,
  IsActiveEntity BOOLEAN,
  HasActiveEntity BOOLEAN,
  HasDraftEntity BOOLEAN,
  DraftAdministrativeData_DraftUUID NVARCHAR(36) NOT NULL,
  PRIMARY KEY(ID)
);

CREATE TABLE DRAFT_DraftAdministrativeData (
  DraftUUID NVARCHAR(36) NOT NULL,
  CreationDateTime TIMESTAMP,
  CreatedByUser NVARCHAR(256),
  DraftIsCreatedByMe BOOLEAN,
  LastChangeDateTime TIMESTAMP,
  LastChangedByUser NVARCHAR(256),
  InProcessByUser NVARCHAR(256),
  DraftIsProcessedByMe BOOLEAN,
  PRIMARY KEY(DraftUUID)
);

CREATE TABLE my_bookshop_Authors (
  modifiedAt TIMESTAMP,
  createdAt TIMESTAMP,
  createdBy NVARCHAR(255),
  modifiedBy NVARCHAR(255),
  ID INTEGER NOT NULL,
  name NVARCHAR(111),
  dateOfBirth DATE,
  dateOfDeath DATE,
  placeOfBirth NVARCHAR(5000),
  placeOfDeath NVARCHAR(5000),
  PRIMARY KEY(ID)
);

CREATE TABLE my_bookshop_Books (
  modifiedAt TIMESTAMP,
  createdAt TIMESTAMP,
  createdBy NVARCHAR(255),
  modifiedBy NVARCHAR(255),
  ID INTEGER NOT NULL,
  title NVARCHAR(111),
  descr NVARCHAR(1111),
  stock INTEGER,
  price DECIMAL(9, 2),
  author_ID INTEGER,
  currency_code NVARCHAR(3),
  PRIMARY KEY(ID)
);

CREATE TABLE my_bookshop_Books_texts (
  locale NVARCHAR(5) NOT NULL,
  ID INTEGER NOT NULL,
  title NVARCHAR(111),
  descr NVARCHAR(1111),
  PRIMARY KEY(locale, ID)
);

CREATE TABLE my_bookshop_OrderItems (
  ID NVARCHAR(36) NOT NULL,
  amount INTEGER,
  netAmount DECIMAL(9, 2),
  parent_ID NVARCHAR(36),
  book_ID INTEGER,
  PRIMARY KEY(ID)
);

CREATE TABLE my_bookshop_Orders (
  ID NVARCHAR(36) NOT NULL,
  modifiedAt TIMESTAMP,
  createdAt TIMESTAMP,
  createdBy NVARCHAR(255),
  modifiedBy NVARCHAR(255),
  OrderNo NVARCHAR(5000),
  total DECIMAL(9, 2),
  currency_code NVARCHAR(3),
  PRIMARY KEY(ID)
);

CREATE TABLE sap_common_Currencies (
  name NVARCHAR(255),
  descr NVARCHAR(1000),
  code NVARCHAR(3) NOT NULL,
  symbol NVARCHAR(2),
  PRIMARY KEY(code)
);

CREATE TABLE sap_common_Currencies_texts (
  locale NVARCHAR(5) NOT NULL,
  name NVARCHAR(255),
  descr NVARCHAR(1000),
  code NVARCHAR(3) NOT NULL,
  PRIMARY KEY(locale, code)
);

CREATE VIEW AdminService_Authors AS SELECT
  Authors_0.modifiedAt,
  Authors_0.createdAt,
  Authors_0.createdBy,
  Authors_0.modifiedBy,
  Authors_0.ID,
  Authors_0.name,
  Authors_0.dateOfBirth,
  Authors_0.dateOfDeath,
  Authors_0.placeOfBirth,
  Authors_0.placeOfDeath
FROM my_bookshop_Authors AS Authors_0;

CREATE VIEW AdminService_Books AS SELECT
  Books_0.modifiedAt,
  Books_0.createdAt,
  Books_0.createdBy,
  Books_0.modifiedBy,
  Books_0.ID,
  Books_0.title,
  Books_0.descr,
  Books_0.stock,
  Books_0.price,
  Books_0.author_ID,
  Books_0.currency_code
FROM my_bookshop_Books AS Books_0;

CREATE VIEW AdminService_Books_texts AS SELECT
  Books_texts_0.locale,
  Books_texts_0.ID,
  Books_texts_0.title,
  Books_texts_0.descr
FROM my_bookshop_Books_texts AS Books_texts_0;

CREATE VIEW AdminService_Currencies AS SELECT
  Currencies_0.name,
  Currencies_0.descr,
  Currencies_0.code,
  Currencies_0.symbol
FROM sap_common_Currencies AS Currencies_0;

CREATE VIEW AdminService_Currencies_texts AS SELECT
  Currencies_texts_0.locale,
  Currencies_texts_0.name,
  Currencies_texts_0.descr,
  Currencies_texts_0.code
FROM sap_common_Currencies_texts AS Currencies_texts_0;

CREATE VIEW AdminService_DraftAdministrativeData AS SELECT
  DraftAdministrativeData.DraftUUID,
  DraftAdministrativeData.CreationDateTime,
  DraftAdministrativeData.CreatedByUser,
  DraftAdministrativeData.DraftIsCreatedByMe,
  DraftAdministrativeData.LastChangeDateTime,
  DraftAdministrativeData.LastChangedByUser,
  DraftAdministrativeData.InProcessByUser,
  DraftAdministrativeData.DraftIsProcessedByMe
FROM DRAFT_DraftAdministrativeData AS DraftAdministrativeData;

CREATE VIEW AdminService_OrderItems AS SELECT
  OrderItems_0.ID,
  OrderItems_0.amount,
  OrderItems_0.netAmount,
  OrderItems_0.parent_ID,
  OrderItems_0.book_ID
FROM my_bookshop_OrderItems AS OrderItems_0;

CREATE VIEW AdminService_Orders AS SELECT
  Orders_0.ID,
  Orders_0.modifiedAt,
  Orders_0.createdAt,
  Orders_0.createdBy,
  Orders_0.modifiedBy,
  Orders_0.OrderNo,
  Orders_0.total,
  Orders_0.currency_code
FROM my_bookshop_Orders AS Orders_0;

CREATE VIEW CatalogService_Authors AS SELECT
  Authors_0.modifiedAt,
  Authors_0.createdAt,
  Authors_0.ID,
  Authors_0.name,
  Authors_0.dateOfBirth,
  Authors_0.dateOfDeath,
  Authors_0.placeOfBirth,
  Authors_0.placeOfDeath
FROM my_bookshop_Authors AS Authors_0;

CREATE VIEW CatalogService_Books AS SELECT
  Books_0.modifiedAt,
  Books_0.createdAt,
  Books_0.ID,
  Books_0.title,
  Books_0.descr,
  Books_0.stock,
  Books_0.price,
  Books_0.author_ID,
  Books_0.currency_code
FROM my_bookshop_Books AS Books_0;

CREATE VIEW CatalogService_Books_texts AS SELECT
  Books_texts_0.locale,
  Books_texts_0.ID,
  Books_texts_0.title,
  Books_texts_0.descr
FROM my_bookshop_Books_texts AS Books_texts_0;

CREATE VIEW CatalogService_Currencies AS SELECT
  Currencies_0.name,
  Currencies_0.descr,
  Currencies_0.code,
  Currencies_0.symbol
FROM sap_common_Currencies AS Currencies_0;

CREATE VIEW CatalogService_Currencies_texts AS SELECT
  Currencies_texts_0.locale,
  Currencies_texts_0.name,
  Currencies_texts_0.descr,
  Currencies_texts_0.code
FROM sap_common_Currencies_texts AS Currencies_texts_0;

CREATE VIEW CatalogService_OrderItems AS SELECT
  OrderItems_0.ID,
  OrderItems_0.amount,
  OrderItems_0.netAmount,
  OrderItems_0.parent_ID,
  OrderItems_0.book_ID
FROM my_bookshop_OrderItems AS OrderItems_0;

CREATE VIEW CatalogService_Orders AS SELECT
  Orders_0.ID,
  Orders_0.modifiedAt,
  Orders_0.createdAt,
  Orders_0.createdBy,
  Orders_0.modifiedBy,
  Orders_0.OrderNo,
  Orders_0.total,
  Orders_0.currency_code
FROM my_bookshop_Orders AS Orders_0;

CREATE VIEW localized_AdminService_Books AS SELECT
  Books_0.modifiedAt,
  Books_0.createdAt,
  Books_0.createdBy,
  Books_0.modifiedBy,
  Books_0.ID,
  Books_0.title,
  Books_0.descr,
  Books_0.stock,
  Books_0.price,
  Books_0.author_ID,
  Books_0.currency_code
FROM localized_my_bookshop_Books AS Books_0;

CREATE VIEW localized_AdminService_Currencies AS SELECT
  Currencies_0.name,
  Currencies_0.descr,
  Currencies_0.code,
  Currencies_0.symbol
FROM localized_sap_common_Currencies AS Currencies_0;

CREATE VIEW localized_CatalogService_Books AS SELECT
  Books_0.modifiedAt,
  Books_0.createdAt,
  Books_0.ID,
  Books_0.title,
  Books_0.descr,
  Books_0.stock,
  Books_0.price,
  Books_0.author_ID,
  Books_0.currency_code
FROM localized_my_bookshop_Books AS Books_0;

CREATE VIEW localized_CatalogService_Currencies AS SELECT
  Currencies_0.name,
  Currencies_0.descr,
  Currencies_0.code,
  Currencies_0.symbol
FROM localized_sap_common_Currencies AS Currencies_0;

CREATE VIEW localized_my_bookshop_Authors AS SELECT
  L_0.modifiedAt,
  L_0.createdAt,
  L_0.createdBy,
  L_0.modifiedBy,
  L_0.ID,
  L_0.name,
  L_0.dateOfBirth,
  L_0.dateOfDeath,
  L_0.placeOfBirth,
  L_0.placeOfDeath
FROM my_bookshop_Authors AS L_0;

CREATE VIEW localized_my_bookshop_Books AS SELECT
  L_0.modifiedAt,
  L_0.createdAt,
  L_0.createdBy,
  L_0.modifiedBy,
  L_0.ID,
  COALESCE(localized_1.title, L_0.title) AS title,
  COALESCE(localized_1.descr, L_0.descr) AS descr,
  L_0.stock,
  L_0.price,
  L_0.author_ID,
  L_0.currency_code
FROM (my_bookshop_Books AS L_0 LEFT JOIN my_bookshop_Books_texts AS localized_1 ON localized_1.ID = L_0.ID AND localized_1.locale = 'en');

CREATE VIEW localized_my_bookshop_OrderItems AS SELECT
  L_0.ID,
  L_0.amount,
  L_0.netAmount,
  L_0.parent_ID,
  L_0.book_ID
FROM my_bookshop_OrderItems AS L_0;

CREATE VIEW localized_my_bookshop_Orders AS SELECT
  L_0.ID,
  L_0.modifiedAt,
  L_0.createdAt,
  L_0.createdBy,
  L_0.modifiedBy,
  L_0.OrderNo,
  L_0.total,
  L_0.currency_code
FROM my_bookshop_Orders AS L_0;

CREATE VIEW localized_sap_common_Currencies AS SELECT
  COALESCE(localized_1.name, L_0.name) AS name,
  COALESCE(localized_1.descr, L_0.descr) AS descr,
  L_0.code,
  L_0.symbol
FROM (sap_common_Currencies AS L_0 LEFT JOIN sap_common_Currencies_texts AS localized_1 ON localized_1.code = L_0.code AND localized_1.locale = 'en');

CREATE VIEW localized_de_AdminService_Books AS SELECT
  Books_0.modifiedAt,
  Books_0.createdAt,
  Books_0.createdBy,
  Books_0.modifiedBy,
  Books_0.ID,
  Books_0.title,
  Books_0.descr,
  Books_0.stock,
  Books_0.price,
  Books_0.author_ID,
  Books_0.currency_code
FROM localized_de_my_bookshop_Books AS Books_0;

CREATE VIEW localized_fr_AdminService_Books AS SELECT
  Books_0.modifiedAt,
  Books_0.createdAt,
  Books_0.createdBy,
  Books_0.modifiedBy,
  Books_0.ID,
  Books_0.title,
  Books_0.descr,
  Books_0.stock,
  Books_0.price,
  Books_0.author_ID,
  Books_0.currency_code
FROM localized_fr_my_bookshop_Books AS Books_0;

CREATE VIEW localized_de_AdminService_Currencies AS SELECT
  Currencies_0.name,
  Currencies_0.descr,
  Currencies_0.code,
  Currencies_0.symbol
FROM localized_de_sap_common_Currencies AS Currencies_0;

CREATE VIEW localized_fr_AdminService_Currencies AS SELECT
  Currencies_0.name,
  Currencies_0.descr,
  Currencies_0.code,
  Currencies_0.symbol
FROM localized_fr_sap_common_Currencies AS Currencies_0;

CREATE VIEW localized_de_CatalogService_Books AS SELECT
  Books_0.modifiedAt,
  Books_0.createdAt,
  Books_0.ID,
  Books_0.title,
  Books_0.descr,
  Books_0.stock,
  Books_0.price,
  Books_0.author_ID,
  Books_0.currency_code
FROM localized_de_my_bookshop_Books AS Books_0;

CREATE VIEW localized_fr_CatalogService_Books AS SELECT
  Books_0.modifiedAt,
  Books_0.createdAt,
  Books_0.ID,
  Books_0.title,
  Books_0.descr,
  Books_0.stock,
  Books_0.price,
  Books_0.author_ID,
  Books_0.currency_code
FROM localized_fr_my_bookshop_Books AS Books_0;

CREATE VIEW localized_de_CatalogService_Currencies AS SELECT
  Currencies_0.name,
  Currencies_0.descr,
  Currencies_0.code,
  Currencies_0.symbol
FROM localized_de_sap_common_Currencies AS Currencies_0;

CREATE VIEW localized_fr_CatalogService_Currencies AS SELECT
  Currencies_0.name,
  Currencies_0.descr,
  Currencies_0.code,
  Currencies_0.symbol
FROM localized_fr_sap_common_Currencies AS Currencies_0;

CREATE VIEW localized_de_my_bookshop_Authors AS SELECT
  L_0.modifiedAt,
  L_0.createdAt,
  L_0.createdBy,
  L_0.modifiedBy,
  L_0.ID,
  L_0.name,
  L_0.dateOfBirth,
  L_0.dateOfDeath,
  L_0.placeOfBirth,
  L_0.placeOfDeath
FROM my_bookshop_Authors AS L_0;

CREATE VIEW localized_fr_my_bookshop_Authors AS SELECT
  L_0.modifiedAt,
  L_0.createdAt,
  L_0.createdBy,
  L_0.modifiedBy,
  L_0.ID,
  L_0.name,
  L_0.dateOfBirth,
  L_0.dateOfDeath,
  L_0.placeOfBirth,
  L_0.placeOfDeath
FROM my_bookshop_Authors AS L_0;

CREATE VIEW localized_de_my_bookshop_Books AS SELECT
  L_0.modifiedAt,
  L_0.createdAt,
  L_0.createdBy,
  L_0.modifiedBy,
  L_0.ID,
  COALESCE(localized_de_1.title, L_0.title) AS title,
  COALESCE(localized_de_1.descr, L_0.descr) AS descr,
  L_0.stock,
  L_0.price,
  L_0.author_ID,
  L_0.currency_code
FROM (my_bookshop_Books AS L_0 LEFT JOIN my_bookshop_Books_texts AS localized_de_1 ON localized_de_1.ID = L_0.ID AND localized_de_1.locale = 'de');

CREATE VIEW localized_fr_my_bookshop_Books AS SELECT
  L_0.modifiedAt,
  L_0.createdAt,
  L_0.createdBy,
  L_0.modifiedBy,
  L_0.ID,
  COALESCE(localized_fr_1.title, L_0.title) AS title,
  COALESCE(localized_fr_1.descr, L_0.descr) AS descr,
  L_0.stock,
  L_0.price,
  L_0.author_ID,
  L_0.currency_code
FROM (my_bookshop_Books AS L_0 LEFT JOIN my_bookshop_Books_texts AS localized_fr_1 ON localized_fr_1.ID = L_0.ID AND localized_fr_1.locale = 'fr');

CREATE VIEW localized_de_my_bookshop_OrderItems AS SELECT
  L_0.ID,
  L_0.amount,
  L_0.netAmount,
  L_0.parent_ID,
  L_0.book_ID
FROM my_bookshop_OrderItems AS L_0;

CREATE VIEW localized_fr_my_bookshop_OrderItems AS SELECT
  L_0.ID,
  L_0.amount,
  L_0.netAmount,
  L_0.parent_ID,
  L_0.book_ID
FROM my_bookshop_OrderItems AS L_0;

CREATE VIEW localized_de_my_bookshop_Orders AS SELECT
  L_0.ID,
  L_0.modifiedAt,
  L_0.createdAt,
  L_0.createdBy,
  L_0.modifiedBy,
  L_0.OrderNo,
  L_0.total,
  L_0.currency_code
FROM my_bookshop_Orders AS L_0;

CREATE VIEW localized_fr_my_bookshop_Orders AS SELECT
  L_0.ID,
  L_0.modifiedAt,
  L_0.createdAt,
  L_0.createdBy,
  L_0.modifiedBy,
  L_0.OrderNo,
  L_0.total,
  L_0.currency_code
FROM my_bookshop_Orders AS L_0;

CREATE VIEW localized_de_sap_common_Currencies AS SELECT
  COALESCE(localized_de_1.name, L_0.name) AS name,
  COALESCE(localized_de_1.descr, L_0.descr) AS descr,
  L_0.code,
  L_0.symbol
FROM (sap_common_Currencies AS L_0 LEFT JOIN sap_common_Currencies_texts AS localized_de_1 ON localized_de_1.code = L_0.code AND localized_de_1.locale = 'de');

CREATE VIEW localized_fr_sap_common_Currencies AS SELECT
  COALESCE(localized_fr_1.name, L_0.name) AS name,
  COALESCE(localized_fr_1.descr, L_0.descr) AS descr,
  L_0.code,
  L_0.symbol
FROM (sap_common_Currencies AS L_0 LEFT JOIN sap_common_Currencies_texts AS localized_fr_1 ON localized_fr_1.code = L_0.code AND localized_fr_1.locale = 'fr');

