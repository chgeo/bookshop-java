using my.bookshop as db from '../db/schema';

@requires:'admin'
service AdminService {
  
  @Capabilities.Insertable: false
  entity Books as projection on db.Books;
  
  entity Authors as projection on db.Authors;
  
  entity Orders as select from db.Orders;
  annotate Orders with @odata.draft.enabled;
}
