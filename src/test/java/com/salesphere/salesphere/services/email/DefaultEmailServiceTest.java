package com.salesphere.salesphere.services.email;

import com.salesphere.salesphere.exceptions.EmailSendingException;
import com.salesphere.salesphere.exceptions.EmailSendingTestException;
import com.salesphere.salesphere.models.Availability;
import com.salesphere.salesphere.models.Category;
import com.salesphere.salesphere.models.Product;
import com.salesphere.salesphere.services.report.ReportService;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DefaultEmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private ReportService reportService;

    @InjectMocks
    private DefaultEmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should send low stock alert email with PDF and Excel attachments")
    void shouldSendLowStockAlertEmailWithAttachments() throws Exception {
        // Given
        Category category = new Category(); // Mock or initialize as needed
        Availability availability = new Availability(); // Mock or initialize as needed
        Product product = new Product(null, "Product1", "Description", "Brand", 10.0, 15.0, 5L, 2L, "SKU123", category, availability);
        List<Product> products = Collections.singletonList(product);
        MimeMessage mimeMessage = mock(MimeMessage.class);
        ByteArrayInputStream pdfInputStream = new ByteArrayInputStream(new byte[]{});
        ByteArrayInputStream excelInputStream = new ByteArrayInputStream(new byte[]{});

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(reportService.generatePdfLowStockReport(any())).thenReturn(pdfInputStream);
        when(reportService.generateExcelLowStockReport(any())).thenReturn(excelInputStream);

        // When
        emailService.sendLowStockAlert(products);

        // Then
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    @DisplayName("Should build email content with product details")
    void shouldBuildEmailContentWithProductDetails() {
        // Given
        Category category = new Category(); // Mock or initialize as needed
        Availability availability = new Availability(); // Mock or initialize as needed
        Product product = new Product(null, "Product1", "Description", "Brand", 10.0, 15.0, 5L, 2L, "SKU123", category, availability);
        List<Product> products = Collections.singletonList(product);

        // When
        String emailContent = emailService.buildEmailContent(products);

        // Then
        assertTrue(emailContent.contains("Alerta de Estoque Baixo"), "Email content should contain 'Low Stock Alert'");
        assertTrue(emailContent.contains("Product1"), "Email content should contain the product name");
        assertTrue(emailContent.contains("Description"), "Email content should contain the product description");
        assertTrue(emailContent.contains("Brand"), "Email content should contain the product brand");
        assertTrue(emailContent.contains("SKU123"), "Email content should contain the product SKU");
        assertTrue(emailContent.contains("10,00"), "Email content should contain the product purchase price");
        assertTrue(emailContent.contains("15,00"), "Email content should contain the product sale price");
        assertTrue(emailContent.contains("5"), "Email content should contain the stock quantity");
        assertTrue(emailContent.contains("2"), "Email content should contain the minimum quantity");
    }

    @Test
    @DisplayName("Should handle an empty product list gracefully")
    void shouldHandleEmptyProductList() throws Exception {
        // Given
        List<Product> products = Collections.emptyList();
        MimeMessage mimeMessage = mock(MimeMessage.class);
        ByteArrayInputStream pdfInputStream = new ByteArrayInputStream(new byte[]{});
        ByteArrayInputStream excelInputStream = new ByteArrayInputStream(new byte[]{});

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(reportService.generatePdfLowStockReport(any())).thenReturn(pdfInputStream);
        when(reportService.generateExcelLowStockReport(any())).thenReturn(excelInputStream);

        // When
        emailService.sendLowStockAlert(products);

        // Then
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    @DisplayName("Should throw EmailSendingException when sending email fails")
    void shouldThrowEmailSendingExceptionWhenSendingEmailFails() {
        // Given
        Category category = new Category(); // Mock or initialize as needed
        Availability availability = new Availability(); // Mock or initialize as needed
        Product product = new Product(null, "Product1", "Description", "Brand", 10.0, 15.0, 5L, 2L, "SKU123", category, availability);
        List<Product> products = Collections.singletonList(product);
        MimeMessage mimeMessage = mock(MimeMessage.class);
        ByteArrayInputStream pdfInputStream = new ByteArrayInputStream(new byte[]{});
        ByteArrayInputStream excelInputStream = new ByteArrayInputStream(new byte[]{});

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(reportService.generatePdfLowStockReport(any())).thenReturn(pdfInputStream);
        when(reportService.generateExcelLowStockReport(any())).thenReturn(excelInputStream);

        doThrow(new EmailSendingTestException("Error sending email")).when(mailSender).send(any(MimeMessage.class));

        // When
        EmailSendingException exception = assertThrows(EmailSendingException.class, () -> {
            emailService.sendLowStockAlert(products);
        });

        // Then
        assertEquals("Failed to send email alert for low stock.", exception.getMessage());
    }
}