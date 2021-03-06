package be.gcroes.thesis.docproc.gae;

import static be.gcroes.thesis.docproc.gae.entity.OfyService.ofy;
import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import be.gcroes.thesis.docproc.gae.entity.Job;
import be.gcroes.thesis.docproc.gae.entity.ShardedCounter;
import be.gcroes.thesis.docproc.gae.entity.Task;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.apphosting.api.ApiProxy.OverQuotaException;
import com.googlecode.objectify.Key;

public class MailServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7988054640172271016L;

	private static final Logger logger = Logger.getLogger(MailServlet.class
			.getCanonicalName());
	
	@Override
	public void init() throws ServletException {
		ofy();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		final Long taskId = Long.parseLong(req.getParameter("taskId"));
		Long jobId = Long.parseLong(req.getParameter("jobId"));
		Key<Job> jobKey = Key.create(Job.class, jobId);
		Key<Task> taskKey = Key.create(Task.class, taskId);
		final Job job = ofy().load().key(jobKey).now();
		Task task = ofy().load().key(taskKey).now();

		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		String msgBody = "A document has been generated for you.\n Click this link to download it: http://storage.googleapis.com/docproc-test.appspot.com/"
				+ task.getResult();

		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(
					"delivery@docproc-test.appspotmail.com", "Docproc"));
			String email = (String) task.getParams().get("email");
			
			String name = (String) task.getParams().get("name");
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
					email, name));
			msg.setSubject("Docproc document generated");
			msg.setText(msgBody);
			if(!email.equals("glenn@xx.org")){ //don't waste my quota :)
				Transport.send(msg);
			}
			logger.info("Sent mail to " + email);

		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch(OverQuotaException oqe){
			logger.info("Mail quota exceeded");
		}
		logger.info("Sent mail for task " + taskId);

		ShardedCounter counter = ofy().load().key(Key.create(ShardedCounter.class, "" + job.getId())).now();
		counter.increment();
		int nTasks = job.getNbOfTasks();
		int count = counter.getCount();
		if ( count == nTasks) {
			logger.info("Placing job " + jobId + " in zip queue");
			Queue queue = QueueFactory.getQueue("zip-queue");
			queue.add(withUrl("/zip").param("jobId", "" + jobId));

		}
		logger.info("Joins remaining: " + (nTasks - count) );

	}

}
