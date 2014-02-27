package be.gcroes.thesis.docproc.gae;

import static be.gcroes.thesis.docproc.gae.entity.OfyService.ofy;

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
import be.gcroes.thesis.docproc.gae.entity.Task;

import com.googlecode.objectify.Key;

public class MailServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7988054640172271016L;
	
	private static final Logger logger = Logger
			.getLogger(MailServlet.class.getCanonicalName());
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		Long taskId = Long.parseLong(req.getParameter("taskId"));
		Long jobId = Long.parseLong(req.getParameter("jobId"));
		Key<Job> jobKey = Key.create(Job.class, jobId);
		Key<Task> taskKey = Key.create(jobKey, Task.class, taskId);
		Task task = ofy().load().key(taskKey).now();

		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		
		String msgBody = "A document has been generated for you.\n Click this link to download it: http://storage.googleapis.com/docproc-test.appspot.com/" + task.getResult();
		
		try {
		    Message msg = new MimeMessage(session);
		    msg.setFrom(new InternetAddress("no-reply@docproc-test.appspotmail.com", "Docproc"));
		    msg.addRecipient(Message.RecipientType.TO,
		     new InternetAddress((String) task.getParams().get("email"), (String) task.getParams().get("name")));
		    msg.setSubject("Docproc document generated");
		    msg.setText(msgBody);
		    Transport.send(msg);

		} catch (AddressException e) {
		    e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
		logger.info("Sent mail for task " + taskId);
	}

}
