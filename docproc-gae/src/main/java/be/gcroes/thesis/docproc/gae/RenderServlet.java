package be.gcroes.thesis.docproc.gae;

import static be.gcroes.thesis.docproc.gae.entity.OfyService.ofy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.googlecode.objectify.Key;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
//import org.apache.fop.apps.FOPException;
//import org.apache.fop.apps.FOUserAgent;
//import org.apache.fop.apps.Fop;
//import org.apache.fop.apps.FopFactory;
//import org.apache.xmlgraphics.util.MimeConstants;
//import org.apache.xmlgraphics.util.uri.CommonURIResolver;
import be.gcroes.thesis.docproc.gae.entity.Job;
import be.gcroes.thesis.docproc.gae.entity.Task;

public class RenderServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3552299743570004114L;

	private final GcsService gcsService = GcsServiceFactory
			.createGcsService(RetryParams.getDefaultInstance());

	private static final Logger logger = Logger
			.getLogger(CsvToDataServlet.class.getCanonicalName());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Long taskId = Long.parseLong(req.getParameter("taskId"));
		Long jobId = Long.parseLong(req.getParameter("jobId"));
		Key<Job> jobKey = Key.create(Job.class, jobId);
		Key<Task> taskKey = Key.create(jobKey, Task.class, taskId);
		Task task = ofy().load().key(taskKey).now();
		String currentTemplate = task.getTemplate();
		GcsOutputChannel outputChannel = gcsService.createOrReplace(
				new GcsFilename("docproc-test.appspot.com", "docproc-" + taskId
						+ ".pdf"), GcsFileOptions.getDefaultInstance());

		try {
			Document document = new Document();
			ByteArrayOutputStream boas = new ByteArrayOutputStream();
			PdfWriter writer = PdfWriter.getInstance(document, boas);
			document.open();
			XMLWorkerHelper.getInstance().parseXHtml(writer, document,
					new ByteArrayInputStream(currentTemplate.getBytes()));
			document.close();

			outputChannel.write(ByteBuffer.wrap(boas.toByteArray()));
			task.setResult(outputChannel.getFilename().getObjectName());
			outputChannel.close();
			ofy().save().entity(task).now();
			logger.info("Rendered XSL for task " + taskId);
		} catch (IOException | DocumentException e) {
			e.printStackTrace();
		}

	}

}