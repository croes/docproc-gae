package be.gcroes.thesis.docproc.gae.entity;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;

public class csvToDataTest {
	
	static {
	    dir = System.getProperty("user.dir") + "/src/main/webapp/WEB-INF/queue.xml";

	}
	
	private static String dir;
	
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
	        new LocalDatastoreServiceTestConfig(),
	        new LocalTaskQueueTestConfig().setQueueXmlPath(dir));
	
	@Before
	public void setUp() {
		helper.setUp();
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}

	
	public void csvToDataWorkerTest() throws IOException{
		String template = "testtemplate";
		String csv = "name;email;header";
		Queue queue = QueueFactory.getQueue("csvtodata-queue");
		queue.add(withUrl("/csvtodata")
				.param("template", template)
				.param("csv", csv));
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
