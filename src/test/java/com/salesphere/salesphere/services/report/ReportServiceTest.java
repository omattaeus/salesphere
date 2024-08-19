package com.salesphere.salesphere.services.report;

import com.itextpdf.text.DocumentException;
import com.salesphere.salesphere.models.product.Product;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReportServiceTest {

    private ReportService reportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reportService = new ReportService();
    }

    @Test
    @DisplayName("Should generate a PDF report with low stock products")
    void shouldGeneratePdfLowStockReport() throws IOException, DocumentException {
        // Given
        Product product1 = new Product(1L, "Product 1", "Description 1", "Brand 1", 10.0, 20.0, 5L, 2L, "SKU1", null, null);
        Product product2 = new Product(2L, "Product 2", "Description 2", "Brand 2", 15.0, 25.0, 3L, 1L, "SKU2", null, null);
        List<Product> products = List.of(product1, product2);

        // When
        ByteArrayInputStream pdfStream = reportService.generatePdfLowStockReport(products);
        byte[] pdfBytes = StreamUtils.copyToByteArray(pdfStream);

        // Then
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    @DisplayName("Should generate an Excel report with low stock products")
    void shouldGenerateExcelLowStockReport() throws IOException {
        // Given
        Product product1 = new Product(1L, "Product 1", "Description 1", "Brand 1", 10.0, 20.0, 5L, 2L, "SKU1", null, null);
        Product product2 = new Product(2L, "Product 2", "Description 2", "Brand 2", 15.0, 25.0, 3L, 1L, "SKU2", null, null);
        List<Product> products = List.of(product1, product2);

        // When
        ByteArrayInputStream excelStream = reportService.generateExcelLowStockReport(products);
        XSSFWorkbook workbook = new XSSFWorkbook(excelStream);
        Sheet sheet = workbook.getSheetAt(0);

        // Then
        assertNotNull(sheet);

        Row headerRow = sheet.getRow(0);
        assertNotNull(headerRow);
        assertEquals("Nome do Produto", headerRow.getCell(0).getStringCellValue());
        assertEquals("Descrição", headerRow.getCell(1).getStringCellValue());

        assertEquals(3, sheet.getPhysicalNumberOfRows());

        Row row1 = sheet.getRow(1);
        assertNotNull(row1);
        assertEquals("Product 1", row1.getCell(0).getStringCellValue());
        assertEquals("Description 1", row1.getCell(1).getStringCellValue());

        Row row2 = sheet.getRow(2);
        assertNotNull(row2);
        assertEquals("Product 2", row2.getCell(0).getStringCellValue());
        assertEquals("Description 2", row2.getCell(1).getStringCellValue());
    }
}