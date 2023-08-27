package com.example.prog4.service;

import com.example.prog4.config.CompanyConf;
import com.example.prog4.model.Employee;
import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

@Service
@AllArgsConstructor
public class PdfService {
    private EmployeeService service;

    private SpringTemplateEngine templateEngine;

    private ClassLoaderTemplateResolver templateResolver;
    public void generatePdfFromHtml(String html, HttpServletResponse response) throws IOException, DocumentException {
        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(pdfOutputStream);
        renderer.finishPDF();

        byte[] pdfBytes = pdfOutputStream.toByteArray();
        pdfOutputStream.close();

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachement; filename=employee.pdf");
        response.setContentLength(pdfBytes.length);

        FileCopyUtils.copy(pdfBytes, response.getOutputStream());
    }

    public String loadThymeleafTemplate(Employee employee) {
        CompanyConf companyConf = new CompanyConf();
        int age = service.calculateAge(employee.getBirthDate());
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        Context context = new Context();
        context.setVariable("employee", employee);
        context.setVariable("age", age);
        context.setVariable("companyConf", companyConf);

        return templateEngine.process("employee_pdf", context);
    }
}
