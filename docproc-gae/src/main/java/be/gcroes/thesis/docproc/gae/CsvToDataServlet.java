package be.gcroes.thesis.docproc.gae;

import static be.gcroes.thesis.docproc.gae.entity.OfyService.ofy;
import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import au.com.bytecode.opencsv.CSVReader;
import be.gcroes.thesis.docproc.gae.entity.Job;
import be.gcroes.thesis.docproc.gae.entity.ShardedCounter;
import be.gcroes.thesis.docproc.gae.entity.Task;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;


public class CsvToDataServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7195713049564077448L;
	
	private static final Logger logger = Logger.getLogger(CsvToDataServlet.class
			.getCanonicalName());
	
	@Override
	public void init() throws ServletException {
		ofy();
	}
	
	
	@Override
	  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
		  throws ServletException, IOException {
		String template = req.getParameter("template");
		String csv = req.getParameter("csv");
		String user = req.getParameter("user");
		Job job = new Job(user, template, csv, new Date(), null);
		ofy().save().entity(job).now();
		
		List<Task> tasks = new ArrayList<Task>();
		CSVReader reader = new CSVReader(new StringReader(csv), ';');
		List<String[]> rows = reader.readAll();
		String[] headers = rows.get(0);
		for(int i=1; i<rows.size();i++){
			String[] row = rows.get(i);
			Task task = job.newTask();
			for(int j=0; j<row.length;j++){
				task.putParam(headers[j], row[j]);
			}
			tasks.add(task);
		}
		reader.close();
		logger.info("Saved job with " + tasks.size() + " tasks");
		ofy().save().entities(tasks).now();
		ofy().save().entity(job).now();
//		
//		//create job join
//		List<Long> taskIds = new ArrayList<Long>();
//		for(Task task : tasks){
//			taskIds.add(task.getId());
//		}
//		Join join = new Join(job.getKey(), taskIds);
//		ofy().save().entity(join).now();
		
		ShardedCounter counter = new ShardedCounter("" + job.getId(), 10);
		ofy().save().entity(counter).now();
		
		logger.info("Placing " + tasks.size() + " tasks in template queue");
		for(Task task : tasks){
			Queue queue = QueueFactory.getQueue("template-queue");
			queue.add(withUrl("/template")
					.param("jobId", "" + job.getId())
					.param("taskId", "" + task.getId()));
			}
		
	}
	

}
