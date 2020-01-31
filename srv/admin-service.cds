using { my.bookshop as my } from '../db/schema';

@path:'admin'
service AdminService  {
  entity Books as projection on my.Books;
  entity Authors as projection on my.Authors;
  entity Orders as select from my.Orders;
}

// // Enable Fiori Draft for Orders
// annotate AdminService.Orders with @odata.draft.enabled;
// annotate AdminService.Books with @Capabilities.Insertable: false;