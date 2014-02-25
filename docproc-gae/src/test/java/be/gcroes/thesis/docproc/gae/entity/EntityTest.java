package be.gcroes.thesis.docproc.gae.entity;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.fest.assertions.api.Fail;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class EntityTest {

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig());

	@Before
	public void setUp() {
		helper.setUp();
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}

	@Test
	public void testCreateOrUpdateJob() {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		assertThat(ds.prepare(new Query("Job")).countEntities(withLimit(10))).isZero();
		Key k1 = Job.createOrUpdateJob("job1", null, null, "\\template", "c;s;v");
		Key k2 = Job.createOrUpdateJob("job2", null, null, "template\\", "v;s;c");
		assertThat(ds.prepare(new Query("Job")).countEntities(withLimit(10))).isEqualTo(2);
		List<Entity> jobs = ds.prepare(new Query("Job")).asList(withLimit(10));
		List<Key> jobkeys = new ArrayList<Key>();
		for(Entity e : jobs){
			jobkeys.add(e.getKey());
		}
		assertThat(jobkeys).contains(k1, k2);
		String newTemplate = "\\anothertemplate";
		String newCsv = "s;c;v";
	    Key newK1 = Job.createOrUpdateJob("job1", null, null, newTemplate, newCsv);
		assertThat(newK1).isEqualTo(k1);
		List<Entity> newJobs = ds.prepare(new Query("Job")).asList(withLimit(10));
		assertThat(newJobs).hasSize(2);
		List<Key> newJobKeys = getKeysOfEntities(newJobs);
		assertThat(newJobKeys).contains(k1, k2, newK1);
		try {
			Entity job1 = ds.get(newK1);
			assertThat(job1.getProperty("template")).isEqualTo(newTemplate);
			assertThat(job1.getProperty("csv")).isEqualTo(newCsv);
		} catch (EntityNotFoundException e1) {
			assertThat(false).isTrue();
		}
		
	}
	
	public List<Key> getKeysOfEntities(List<Entity> entities){
		ArrayList<Key> keys = new ArrayList<Key>();
		for(Entity e : entities){
			keys.add(e.getKey());
		}
		return keys;
	}

}
