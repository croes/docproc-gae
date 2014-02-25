package be.gcroes.thesis.docproc.gae;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;

public class QueueJobServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 24593714312220073L;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			  throws ServletException, IOException {
		String template = req.getParameter("template");
		log("Template: " + template);
		log("Params:");
		for( Enumeration e = req.getParameterNames(); e.hasMoreElements() ;){
			String param = ((String)e.nextElement());
			log("param " + param + ": " + req.getParameter(param));
		}
		String csv = req.getParameter("csv");
		log("Csv: "+ csv);
		Queue queue = QueueFactory.getQueue("csvtodata-queue");
		queue.add(withUrl("/csvtodata")
				.param("template", template)
				.param("csv", csv));
	}

}
