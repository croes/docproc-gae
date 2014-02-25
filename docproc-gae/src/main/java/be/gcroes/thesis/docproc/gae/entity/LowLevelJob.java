package be.gcroes.thesis.docproc.gae.entity;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import be.gcroes.thesis.docproc.gae.Util;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

public class LowLevelJob{
	
	private static final Logger logger = Logger.getLogger(LowLevelJob.class.getCanonicalName());
	
	public static Key createOrUpdateJob(String id,
										 Date startTime,
										 Date endTime,
										 String template,
										 String csv){
		Entity job = LowLevelJob.getJob(id);
		if(job == null){
			job = new Entity("Job", id);
		}
		job.setProperty("startTime", startTime);
		job.setProperty("endTime", endTime);
		job.setProperty("template", template);
		job.setProperty("csv", csv);
		return Util.createOrUpdate(job);
	}
	
	public static Key createOrUpdateJob(Entity updatedJob){
		return Util.createOrUpdate(updatedJob);
	}

	public static Entity getJob(String id) {
		Key key = KeyFactory.createKey("Job", id);
		return Util.findEntity(key);
	}
	
	public static Iterable<Entity> getAllJobs(){
		return Util.listEntities("Job", null, null);
	}
	
	public static List<Entity> getTasks(String id){
		Query query = new Query();
	  	Key parentKey = KeyFactory.createKey("Job", id);
	  	query.setAncestor(parentKey);
	  	//query.addFilter(Entity.KEY_RESERVED_PROPERTY, Query.FilterOperator.GREATER_THAN, parentKey);
	  	query.setFilter(new Query.FilterPredicate(Entity.KEY_RESERVED_PROPERTY, Query.FilterOperator.GREATER_THAN, parentKey));
	  		List<Entity> results = Util.getDatastoreServiceInstance()
	  				.prepare(query).asList(FetchOptions.Builder.withDefaults());
	  		return results;
	}
	
	public static void deleteJob(String id){
		logger.log(Level.INFO, "Deleting job " + id);
		Key key = KeyFactory.createKey("Job", id);
		
		List<Entity> tasks = getTasks(id);
		if(!tasks.isEmpty()){
			logger.log(Level.INFO, "Removing " + tasks.size() + " tasks associated to job " + id);
			for(Entity t : tasks){
				Util.deleteEntity(t.getKey());
			}
		}
		Util.deleteEntity(key);
	}
}
