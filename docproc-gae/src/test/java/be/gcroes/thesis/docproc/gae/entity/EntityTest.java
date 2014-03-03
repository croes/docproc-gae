package be.gcroes.thesis.docproc.gae.entity;

import static be.gcroes.thesis.docproc.gae.entity.OfyService.ofy;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;
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
	public void testObjectify(){
		Job job1 = new Job();
		User user = new User("testuser@test.com", "test.com");
		job1.setUser(user.getUserId());
		job1.setCsv("c;s;v;");
		job1.setTemplate("\\template");
		ofy().save().entity(job1).now();
		Task t1 = job1.newTask();
		t1.putParam("test", "hello world");
		t1.putParam("foo", "bar");
		t1.setResult("all done");
		Task t2 = job1.newTask();
		t2.putParam("test", "the one test to rule them all");
		t2.putParam("another", "more tests");
		t2.setResult("work work");
		ofy().save().entities(job1, t1, t2).now();
		assertThat(job1.getId()).isNotNull();
		assertThat(t1.getId()).isNotNull();
		assertThat(t2.getId()).isNotNull();
		List<Task> jobTasks = job1.getTasks();
		assertThat(jobTasks).contains(t1, t2);
		List<Task> foundTasks = ofy().load().type(Task.class).list();
		assertThat(foundTasks).hasSize(2);
		assertThat(foundTasks).contains(t1,t2);
		int indexT1 = foundTasks.indexOf(t1);
		int indexT2 = foundTasks.indexOf(t2);
		assertThat(foundTasks.get(indexT1).getParams().get("test")).isEqualTo("hello world");
		assertThat(foundTasks.get(indexT2).getParams().get("test")).isEqualTo("the one test to rule them all");
		assertThat(t1.getJob()).isEqualTo(job1);
		assertThat(t2.getJob()).isEqualTo(job1);
		job1.setResult("ALL DONE");
		ofy().save().entities(job1, t1, t2).now();
		List<Job> foundJobs = ofy().load().type(Job.class).limit(10).list();
		assertThat(foundJobs).hasSize(1);
		assertThat(foundJobs.get(0)).isEqualTo(job1);
		assertThat(foundJobs.get(0).getResult()).isEqualTo("ALL DONE");
	}
	
	public List<Key> getKeysOfEntities(List<Entity> entities){
		ArrayList<Key> keys = new ArrayList<Key>();
		for(Entity e : entities){
			keys.add(e.getKey());
		}
		return keys;
	}
	
	@Test
	public void testShardCounter(){
		ShardedCounter counter = new ShardedCounter("job5645645313", 10);
		ofy().save().entity(counter).now();
		List<GeneralCounterShard> shards = ofy().load().type(GeneralCounterShard.class).list();
		assertThat(shards).hasSize(10);
		for(int i = 0; i < 15; i++){
			counter.increment();
		}
		assertThat(counter.getCount()).isEqualTo(15);
	}

}
