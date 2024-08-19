package com.salesphere.salesphere.services.report;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.salesphere.salesphere.models.product.Product;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class ReportService {

    public ByteArrayInputStream generatePdfLowStockReport(List<Product> products) {
        Document document = new Document();
        ByteArrayOutputStream pdfOut = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, pdfOut);
            document.open();

            com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BaseColor.DARK_GRAY);
            Paragraph title = new Paragraph("Relatório de Estoque Baixo", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph(" "));

            com.itextpdf.text.Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.DARK_GRAY);
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

            com.itextpdf.text.Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.DARK_GRAY);
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
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Estoque Baixo");

            CellStyle headerStyle = createHeaderCellStyle(workbook);
            CellStyle dataStyle = createDataCellStyle(workbook);
            CellStyle borderStyle = createBorderCellStyle(workbook);
            CellStyle suggestionStyle = createSuggestionCellStyle(workbook);
            CellStyle variationStyle = createVariationCellStyle(workbook);

            Row header = sheet.createRow(0);
            String[] headers = {"Nome do Produto", "Descrição", "Marca", "Categoria", "Preço de Compra", "Preço de Venda", "Quantidade em Estoque", "Quantidade Mínima", "Variação Estoque"};
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
            double previousStock = 0;
            for(Product product : products) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(product.getProductName() != null ? product.getProductName() : "N/A");
                row.createCell(1).setCellValue(product.getDescription() != null ? product.getDescription() : "N/A");
                row.createCell(2).setCellValue(product.getBrand() != null ? product.getBrand() : "N/A");
                row.createCell(3).setCellValue(product.getCategory() != null ? product.getCategory().toString() : "N/A");
                row.createCell(4).setCellValue(product.getPurchasePrice());
                row.createCell(5).setCellValue(product.getSalePrice());
                row.createCell(6).setCellValue(product.getStockQuantity());
                row.createCell(7).setCellValue(product.getMinimumQuantity());

                Cell variationCell = row.createCell(8);
                if (rowNum > 1) {
                    double currentStock = product.getStockQuantity();
                    CellStyle variationCellStyle = workbook.createCellStyle();
                    Font font = workbook.createFont();
                    if (currentStock > previousStock) {
                        font.setColor(IndexedColors.BLUE.getIndex());
                        variationCell.setCellValue("↑");
                    } else if (currentStock < previousStock) {
                        font.setColor(IndexedColors.RED.getIndex());
                        variationCell.setCellValue("↓");
                    } else {
                        font.setColor(IndexedColors.BLACK.getIndex());
                        variationCell.setCellValue("→");
                    }
                    variationCellStyle.setFont(font);
                    variationCell.setCellStyle(variationCellStyle);
                }
                previousStock = product.getStockQuantity();

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

            Row analysisRow = sheet.createRow(rowNum + 3);
            analysisRow.createCell(0).setCellValue("Análise e Sugestões:");
            analysisRow.getCell(0).setCellStyle(suggestionStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowNum + 3, rowNum + 3, 0, 8));
            String analysisText = generateAnalysis(products);
            Cell analysisCell = analysisRow.createCell(0);
            analysisCell.setCellValue(analysisText);
            analysisCell.setCellStyle(suggestionStyle);

            createCharts(sheet, products, rowNum + 6);

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(excelOut);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(excelOut.toByteArray());
    }

    private CellStyle createVariationCellStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        style.setFont(font);
        return style;
    }

    private void createCharts(XSSFSheet sheet, List<Product> products, int startRow) {
        XSSFWorkbook workbook = sheet.getWorkbook();
        XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();

        // Gráfico de Barras
        XDDFChart barChart = drawing.createChart(drawing.createAnchor(0, 0, 0, 0, 0, startRow, 6, startRow + 15));
        XDDFCategoryAxis bottomAxis = barChart.createCategoryAxis(AxisPosition.BOTTOM);
        XDDFValueAxis leftAxis = barChart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle("Preço");

        XDDFDataSource xData = XDDFDataSourcesFactory.fromStringCellRange(sheet, new CellRangeAddress(1, products.size(), 0, 0));
        XDDFNumericalDataSource yData1 = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(1, products.size(), 4, 4));
        XDDFNumericalDataSource yData2 = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(1, products.size(), 5, 5));

        XDDFChartData barChartData = barChart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
        XDDFChartData.Series series1 = barChartData.addSeries(xData, yData1);
        series1.setTitle("Preço de Compra", null);
        XDDFChartData.Series series2 = barChartData.addSeries(xData, yData2);
        series2.setTitle("Preço de Venda", null);

        barChart.plot(barChartData);
        barChart.setTitleText("Preços de Compra e Venda");
        barChart.setTitleOverlay(false);

        // Gráfico de Pizza
        XDDFChart pieChart = drawing.createChart(drawing.createAnchor(0, 0, 0, 0, 0, startRow + 18, 6, startRow + 33));
        XDDFDataSource pieData = XDDFDataSourcesFactory.fromStringCellRange(sheet, new CellRangeAddress(1, products.size(), 0, 0));
        XDDFNumericalDataSource pieValues = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(1, products.size(), 6, 6));

        XDDFChartData pieChartData = pieChart.createData(ChartTypes.PIE, null, null);
        XDDFChartData.Series pieSeries = pieChartData.addSeries(pieData, pieValues);
        pieSeries.setTitle("Quantidade em Estoque", null);

        pieChart.plot(pieChartData);
        pieChart.setTitleText("Distribuição de Quantidade em Estoque");
        pieChart.setTitleOverlay(false);
    }


    private CellStyle createHeaderCellStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createDataCellStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createBorderCellStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createSuggestionCellStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setItalic(true);
        style.setFont(font);
        style.setWrapText(true);
        return style;
    }

    private CellStyle createTitleCellStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private String generateAnalysis(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return "Nenhum produto disponível para análise.";
        }

        double lowestStockThreshold = 10;
        double maxStockValue = Double.MIN_VALUE;
        double minStockValue = Double.MAX_VALUE;
        Product maxStockValueProduct = null;
        Product minStockValueProduct = null;

        double totalValueInStock = 0;
        Map<String, Integer> categoryCountMap = new HashMap<>();
        List<Product> lowStockProducts = new ArrayList<>();

        for (Product product : products) {
            if (product.getStockQuantity() < product.getMinimumQuantity()) {
                lowStockProducts.add(product);
            }

            double stockValue = product.getStockQuantity() * product.getSalePrice();
            if (stockValue > maxStockValue) {
                maxStockValue = stockValue;
                maxStockValueProduct = product;
            }
            if (stockValue < minStockValue) {
                minStockValue = stockValue;
                minStockValueProduct = product;
            }
            totalValueInStock += stockValue;

            String categoryName = product.getCategory() != null ? product.getCategory().toString() : "Desconhecida";
            categoryCountMap.put(categoryName, categoryCountMap.getOrDefault(categoryName, 0) + 1);
        }

        StringBuilder analysis = new StringBuilder();
        analysis.append("Análise de Estoque:\n\n");

        if (!lowStockProducts.isEmpty()) {
            analysis.append("Produtos com Estoque Abaixo do Mínimo:\n");
            for (Product product : lowStockProducts) {
                analysis.append(String.format("- %s (Categoria: %s) - Quantidade em Estoque: %d, Quantidade Mínima: %d\n",
                        product.getProductName(), product.getCategory(), product.getStockQuantity(), product.getMinimumQuantity()));
            }
            analysis.append("\n");
        } else {
            analysis.append("Todos os produtos têm estoque suficiente.\n\n");
        }

        if (maxStockValueProduct != null) {
            analysis.append(String.format("Produto com Maior Valor em Estoque: %s (Categoria: %s) - Valor Total: R$%.2f\n",
                    maxStockValueProduct.getProductName(), maxStockValueProduct.getCategory(), maxStockValue));
        }

        if (minStockValueProduct != null) {
            analysis.append(String.format("Produto com Menor Valor em Estoque: %s (Categoria: %s) - Valor Total: R$%.2f\n",
                    minStockValueProduct.getProductName(), minStockValueProduct.getCategory(), minStockValue));
        }

        analysis.append(String.format("\nValor Total em Estoque: R$%.2f\n", totalValueInStock));

        analysis.append("\nCategorias de Produtos:\n");
        for (Map.Entry<String, Integer> entry : categoryCountMap.entrySet()) {
            analysis.append(String.format("- %s: %d produtos\n", entry.getKey(), entry.getValue()));
        }

        analysis.append("\nSugestões:\n");
        if (lowStockProducts.size() > 0) {
            analysis.append("Recomenda-se verificar os produtos com estoque abaixo do mínimo e considerar a reposição para evitar rupturas de estoque.\n");
        } else {
            analysis.append("O estoque está bem gerido com base nas quantidades mínimas estabelecidas.\n");
        }

        if (totalValueInStock > 100000) {
            analysis.append("O valor total do estoque está alto. Considere revisar a estratégia de compras e vendas para otimizar o capital investido.\n");
        }

        return analysis.toString();
    }
}
