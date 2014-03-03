package be.gcroes.thesis.docproc.gae;

import static be.gcroes.thesis.docproc.gae.entity.OfyService.ofy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import be.gcroes.thesis.docproc.gae.entity.Job;
import be.gcroes.thesis.docproc.gae.entity.Task;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.googlecode.objectify.Key;

public class ZipServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4972802767453781043L;
	
	private GcsService gcsService = null;
	
	private static final Logger logger = Logger.getLogger(ZipServlet.class
			.getCanonicalName());
	
	@Override
	public void init() throws ServletException {
		ofy();
		gcsService = GcsServiceFactory
				.createGcsService(RetryParams.getDefaultInstance());
	}

	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Long jobId = Long.parseLong(req.getParameter("jobId"));
		Key<Job> jobKey = Key.create(Job.class, jobId);
		Job job = ofy().load().key(jobKey).now();
		List<Task> tasks = job.getTasks();
		ByteArrayOutputStream boas = new ByteArrayOutputStream();
		ZipOutputStream zipOS = new ZipOutputStream(boas);
		byte[] buffer = new byte[1024];
		for(Task task : tasks){
			GcsFilename fileName = new GcsFilename("docproc-test.appspot.com", task.getResult());
			GcsInputChannel inputChannel = gcsService.openPrefetchingReadChannel(fileName, 0, 512 * 1024);
			ZipEntry ze = new ZipEntry(task.getResult());
			zipOS.putNextEntry(ze);
			while(inputChannel.read(ByteBuffer.wrap(buffer)) > 0){
				zipOS.write(buffer);
			}
			inputChannel.close();
		}
		zipOS.closeEntry();
		zipOS.close();		
		
		GcsFilename fileName = new GcsFilename("docproc-test.appspot.com", "job-" + job.getId() + ".zip");
		GcsOutputChannel outputChannel = gcsService.createOrReplace(fileName, GcsFileOptions.getDefaultInstance());
		outputChannel.write(ByteBuffer.wrap(boas.toByteArray()));
		outputChannel.close();
		boas.close();
		
		job.setResult(fileName.getObjectName());
		job.setEndTime(new Date());
		ofy().save().entity(job).now();
		
		logger.info("Created zip for job " + job.getId());
	}
	

}
