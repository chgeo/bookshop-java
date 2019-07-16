using from '@sap/capire-bookshop/srv/cat-service';
using my.bookshop as my from '../db/data-model';

extend service CatalogService with {

  @insertonly entity OrderItems as projection on my.OrderItems;

}