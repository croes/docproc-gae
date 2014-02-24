package be.gcroes.thesis.docproc.gae;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Transaction;

public class Util {

	private static final Logger logger = Logger.getLogger(Util.class
			.getCanonicalName());

	private static DatastoreService datastore = DatastoreServiceFactory
			.getDatastoreService();

	public static void createOrUpdate(Entity e) {
		logger.info("Saving entity");
		Transaction txn = datastore.beginTransaction();
		try {
			datastore.put(e);
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}

	public static Entity findEntity(Key key) {
		logger.log(Level.INFO, "Search the entity");
	  	try {	  
	  	  return datastore.get(key);
	  	} catch (EntityNotFoundException e) {
	  	  return null;
	  	}
	}

	public static Iterable<Entity> listEntities(String kind, String searchBy,
			String searchFor) {
		logger.log(Level.INFO, "Search entities based on search criteria");
	  	Query q = new Query(kind);
	  	if (searchFor != null && !"".equals(searchFor)) {
	  	  //q.addFilter(searchBy, FilterOperator.EQUAL, searchFor);
	  	  q.setFilter(new Query.FilterPredicate(searchBy, FilterOperator.EQUAL, searchFor));
	  	}
	  	PreparedQuery pq = datastore.prepare(q);
	  	return pq.asIterable();
	}

	public static DatastoreService getDatastoreServiceInstance() {
		return datastore;
	}

	public static void deleteEntity(Key key) {
		logger.log(Level.INFO, "Deleting entity");
		datastore.delete(key);    
	}
}
