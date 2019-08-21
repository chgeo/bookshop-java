using my.bookshop as db from '../db/schema';

service AdminService @(_requires:'admin') {
  entity Books as projection on db.Books;
  entity Authors as projection on db.Authors;
  entity Orders as select from db.Orders;
  annotate Orders with @odata.draft.enabled;

  //------- auto-exposed --------
    // annotate Orders with { Items @odata.contained; }
    // annotate Books with { texts @odata.contained; }
    // annotate sap.common.Currencies with { texts @odata.contained; }
}