package com.salesphere.salesphere.services.report;

import com.itextpdf.text.Font;
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

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BaseColor.DARK_GRAY);
            Paragraph title = new Paragraph("Relatório de Estoque Baixo", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph(" "));

            Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.DARK_GRAY);
            Paragraph date = new Paragraph("Data do Relatório: " + new java.util.Date(), dateFont);
            date.setAlignment(Element.ALIGN_RIGHT);
            document.add(date);

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

            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.DARK_GRAY);
            Paragraph footer = new Paragraph("Este é um relatório gerado automaticamente.", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(pdfOut.toByteArray());
    }

    private void addPdfTableHeader(PdfPTable table) {
        Stream.of("Nome do Produto", "Descrição", "Marca", "Categoria", "Preço de Compra", "Preço de Venda", "Quantidade em Estoque", "Quantidade Mínima")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setPhrase(new Phrase(columnTitle));
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setPadding(5);
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

    public ByteArrayInputStream generateExcelLowStockReport(List<Product> products) {
        ByteArrayOutputStream excelOut = new ByteArrayOutputStream();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Estoque Baixo");

            CellStyle headerStyle = createHeaderCellStyle(workbook);
            CellStyle dataStyle = createDataCellStyle(workbook);
            CellStyle borderStyle = createBorderCellStyle(workbook);

            Row header = sheet.createRow(0);
            String[] headers = {"Nome do Produto", "Descrição", "Marca", "Categoria", "Preço de Compra", "Preço de Venda", "Quantidade em Estoque", "Quantidade Mínima"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            double totalPurchasePrice = 0;
            double totalSalePrice = 0;
            double totalStockQuantity = 0;
            double totalMinQuantity = 0;
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

                totalPurchasePrice += product.getPurchasePrice();
                totalSalePrice += product.getSalePrice();
                totalStockQuantity += product.getStockQuantity();
                totalMinQuantity += product.getMinimumQuantity();
            }

            Row summaryRow = sheet.createRow(rowNum);
            summaryRow.createCell(0).setCellValue("Total:");
            summaryRow.createCell(4).setCellValue(totalPurchasePrice);
            summaryRow.createCell(5).setCellValue(totalSalePrice);
            summaryRow.createCell(6).setCellValue(totalStockQuantity);
            summaryRow.createCell(7).setCellValue(totalMinQuantity);
            for (int i = 4; i <= 7; i++) {
                summaryRow.getCell(i).setCellStyle(borderStyle);
            }

            Row averageRow = sheet.createRow(rowNum + 1);
            averageRow.createCell(0).setCellValue("Média:");
            averageRow.createCell(4).setCellFormula(String.format("AVERAGE(E2:E%d)", rowNum));
            averageRow.createCell(5).setCellFormula(String.format("AVERAGE(F2:F%d)", rowNum));
            averageRow.createCell(6).setCellFormula(String.format("AVERAGE(G2:G%d)", rowNum));
            averageRow.createCell(7).setCellFormula(String.format("AVERAGE(H2:H%d)", rowNum));
            for (int i = 4; i <= 7; i++) {
                averageRow.getCell(i).setCellStyle(borderStyle);
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(excelOut);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(excelOut.toByteArray());
    }

    private CellStyle createHeaderCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private CellStyle createDataCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createBorderCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
}