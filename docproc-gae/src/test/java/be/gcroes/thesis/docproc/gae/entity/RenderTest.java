package be.gcroes.thesis.docproc.gae.entity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

public class RenderTest {
	
	public static void main(String[] args){
		
		try {
			Document document = new Document();
			ByteArrayOutputStream boas = new ByteArrayOutputStream();
			PdfWriter writer = PdfWriter.getInstance(document, boas);
			document.open();
			String html = "<html><body>Hello world</body></html>";
			XMLWorkerHelper.getInstance().parseXHtml(writer, document, new ByteArrayInputStream(html.getBytes()));
			document.close();
			
			File file = new File("C:\\tmp\\test.pdf");
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(boas.toByteArray());
			fos.close();
		} catch (DocumentException | IOException e) {
			e.printStackTrace();
		}
		
	}

}
