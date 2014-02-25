package be.gcroes.thesis.docproc.gae;

import static be.gcroes.thesis.docproc.gae.entity.OfyService.ofy;

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
import be.gcroes.thesis.docproc.gae.entity.Task;

import com.google.appengine.api.users.User;


public class CsvToDataServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7195713049564077448L;
	
	private static final Logger logger = Logger.getLogger(CsvToDataServlet.class
			.getCanonicalName());
	
	
	@Override
	  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
		  throws ServletException, IOException {
		String template = req.getParameter("template");
		String csv = req.getParameter("csv");
		User user = new User("test@test.com", "test.com");
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
		logger.info("Saved job with " + tasks.size() + " tasks");
		ofy().save().entities(tasks).now();
	}
	

}
