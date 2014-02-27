package be.gcroes.thesis.docproc.gae;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class QueueJobServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 24593714312220073L;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			  throws ServletException, IOException {
		String template = req.getParameter("template");
		String csv = req.getParameter("csv");
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		Queue queue = QueueFactory.getQueue("csvtodata-queue");
		queue.add(withUrl("/csvtodata")
				.param("template", template)
				.param("csv", csv)
				.param("user", user.getUserId()));
	}

}
