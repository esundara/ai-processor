package com.ai.processor.service;

import org.apache.pdfbox.Loader; // New in PDFBox 3.x
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile; // For loading from File
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PdfTextExtractionService {

    private static final Logger logger = LoggerFactory.getLogger(PdfTextExtractionService.class);
    public String extractTextFromPdf(MultipartFile pdfFile) throws IOException {
        if (pdfFile.isEmpty()) {
            return "No PDF file provided.";
        }

        try (InputStream is = pdfFile.getInputStream()){
            byte[] pdfBytes = IOUtils.toByteArray(is);
            try (PDDocument document = Loader.loadPDF(pdfBytes)) {

                if (document.isEncrypted()) {
                    throw new IOException("Encrypted PDF documents are not supported without a password.");
                }

                PDFTextStripper pdfTextStripper = new PDFTextStripper();
                String extractedText = pdfTextStripper.getText(document);
                return extractedText;
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Error extracting text from uploaded PDF: " + e.getMessage());
        }
    }

    public String extractTextFromPdf(File pdfFile) throws IOException {
        if (!pdfFile.exists() || !pdfFile.isFile()) {
            return "Invalid PDF file path or file does not exist.";
        }

        try (// In PDFBox 3.x, use Loader.loadPDF() with a RandomAccessReadBufferedFile
             PDDocument document = Loader.loadPDF(new RandomAccessReadBufferedFile(pdfFile))) {

            if (document.isEncrypted()) {
                throw new IOException("Encrypted PDF documents are not supported without a password.");
            }

            PDFTextStripper pdfTextStripper = new PDFTextStripper();
            String extractedText = pdfTextStripper.getText(document);
            return extractedText;

        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Error extracting text from PDF at path: " + e.getMessage());
        }
    }
}