package be.gcroes.thesis.docproc.gae;


import static be.gcroes.thesis.docproc.gae.entity.OfyService.ofy;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;




import be.gcroes.thesis.docproc.gae.entity.Job;
import be.gcroes.thesis.docproc.gae.entity.Task;

import com.googlecode.objectify.Key;

public class TemplateServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1104579914729480294L;
	
	private static final Logger logger = Logger.getLogger(TemplateServlet.class
			.getCanonicalName());
	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Long taskId = Long.parseLong(req.getParameter("taskId"));
		Long jobId = Long.parseLong(req.getParameter("jobId"));
		Key<Job> jobKey = Key.create(Job.class, jobId);
		Key<Task> taskKey = Key.create(jobKey, Task.class, taskId);
		Job job = ofy().load().key(jobKey).now();
		Task task = ofy().load().key(taskKey).now();
		String template = job.getTemplate();
		String filledInTemplate = null;
		try{
			VelocityContext context = new VelocityContext();

            for (Entry<String, Object> entry : task.getParams().entrySet()) {
                context.put(entry.getKey(), entry.getValue());
            }
            StringWriter sw = new StringWriter();
            
            Velocity.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem"); //necessary!
            Velocity.evaluate(context, sw, "logtag", template);
            
            filledInTemplate = sw.toString();
            
            sw.close();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
        if(filledInTemplate != null){
        	task.setTemplate(filledInTemplate);
        	logger.info("Processed template for task " + task.getId());
        	ofy().save().entity(task).now();
        }else{
        	logger.info("Could not process template for task " + task.getId());
        }
        
        //TODO call next worker
		
	}

}
