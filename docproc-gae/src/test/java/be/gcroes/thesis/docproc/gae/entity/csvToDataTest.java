package be.gcroes.thesis.docproc.gae.entity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.*;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;

public class csvToDataTest {
	
	private static final Logger logger = Logger.getLogger(csvToDataTest.class
			.getCanonicalName());
	
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
		System.out.println(dir);
		 BufferedReader br = new BufferedReader(new FileReader(dir));
		 String line = null;
		 while ((line = br.readLine()) != null) {
		   System.out.println(line);
		 }
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
