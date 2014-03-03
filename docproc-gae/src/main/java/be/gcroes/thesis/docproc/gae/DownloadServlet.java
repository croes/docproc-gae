package be.gcroes.thesis.docproc.gae;

import static be.gcroes.thesis.docproc.gae.entity.OfyService.ofy;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import be.gcroes.thesis.docproc.gae.entity.Job;
import be.gcroes.thesis.docproc.gae.entity.Task;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.tools.cloudstorage.GcsFileMetadata;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.googlecode.objectify.Key;

public class DownloadServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6054667198586643779L;
	
	
	private BlobstoreService blobstoreService = null;
	private GcsService gcsService = null;
	
	private static final Logger logger = Logger.getLogger(DownloadServlet.class
			.getCanonicalName());
	
	
	@Override
	public void init() throws ServletException {
		ofy();
		blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String jobIdstr = req.getParameter("jobId");
		String taskIdstr = req.getParameter("taskId");
		Long jobId = jobIdstr != null ? Long.parseLong(jobIdstr) : null;
		Long taskId = taskIdstr != null ? Long.parseLong(taskIdstr) : null;
		String objectName = null;
		if(jobId != null && taskId == null){
			Key<Job> jobKey = Key.create(Job.class, jobId);
			Job job = ofy().load().key(jobKey).now();
			objectName = job.getResult();
		}
		if(taskId != null){
			Key<Task> taskKey = Key.create(Task.class, taskId);
			Task task = ofy().load().key(taskKey).now();
			objectName = task.getResult();
		}
		if(objectName != null){
			GcsFilename fileName = new GcsFilename("docproc-test.appspot.com", objectName);
			BlobKey blobKey = blobstoreService.createGsBlobKey("/gs/" + fileName.getBucketName() + "/" + fileName.getObjectName());
			GcsFileMetadata meta = gcsService.getMetadata(fileName);
			resp.setContentLength((int)meta.getLength());
			resp.setContentType(meta.getOptions().getMimeType());
			resp.setHeader("Content-Disposition", "attachment; filename=" + fileName.getObjectName());
			blobstoreService.serve(blobKey, resp);
		}
		else{
			resp.getWriter().write("Could not find file for job " + jobId + "or task " + taskId);
		}
		logger.info("Served download for job " + jobId + " or task " + taskId);
	}

}
