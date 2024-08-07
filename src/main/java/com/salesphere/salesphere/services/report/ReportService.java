package com.salesphere.salesphere.services.report;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.salesphere.salesphere.models.Product;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Stream;

@Service
public class ReportService {

    public ByteArrayInputStream generatePdfLowStockReport(List<Product> products) {
        Document document = new Document();
        ByteArrayOutputStream pdfOut = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, pdfOut);
            document.open();
            document.add(new Paragraph("Relatório de Estoque Baixo", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK)));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            addPdfTableHeader(table);
            for (Product product : products) {
                addPdfTableRow(table, product);
            }

            document.add(table);
            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(pdfOut.toByteArray());
    }

    public ByteArrayInputStream generateExcelLowStockReport(List<Product> products) {
        ByteArrayOutputStream excelOut = new ByteArrayOutputStream();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Estoque Baixo");

            Row header = sheet.createRow(0);
            String[] headers = {"Nome do Produto", "Descrição", "Marca", "Categoria", "Preço de Compra", "Preço de Venda", "Quantidade em Estoque", "Quantidade Mínima"};
            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (Product product : products) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(product.getProductName() != null ? product.getProductName() : "N/A");
                row.createCell(1).setCellValue(product.getDescription() != null ? product.getDescription() : "N/A");
                row.createCell(2).setCellValue(product.getBrand() != null ? product.getBrand() : "N/A");
                row.createCell(3).setCellValue(product.getCategory() != null ? product.getCategory().toString() : "N/A");
                row.createCell(4).setCellValue(product.getPurchasePrice());
                row.createCell(5).setCellValue(product.getSalePrice());
                row.createCell(6).setCellValue(product.getStockQuantity());
                row.createCell(7).setCellValue(product.getMinimumQuantity());
            }

            workbook.write(excelOut);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(excelOut.toByteArray());
    }

    private void addPdfTableHeader(PdfPTable table) {
        Stream.of("Nome do Produto", "Descrição", "Marca", "Categoria", "Preço de Compra", "Preço de Venda", "Quantidade em Estoque", "Quantidade Mínima")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }

    private void addPdfTableRow(PdfPTable table, Product product) {
        table.addCell(product.getProductName() != null ? product.getProductName() : "N/A");
        table.addCell(product.getDescription() != null ? product.getDescription() : "N/A");
        table.addCell(product.getBrand() != null ? product.getBrand() : "N/A");
        table.addCell(product.getCategory() != null ? product.getCategory().toString() : "N/A");
        table.addCell(String.format("%.2f", product.getPurchasePrice()));
        table.addCell(String.format("%.2f", product.getSalePrice()));
        table.addCell(String.valueOf(product.getStockQuantity()));
        table.addCell(String.valueOf(product.getMinimumQuantity()));
    }
}