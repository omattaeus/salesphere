package com.salesphere.salesphere.controllers.view;

import com.salesphere.salesphere.services.product.ProductService;
import com.salesphere.salesphere.models.dto.ProductResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class WebController {

    private final ProductService productService;

    public WebController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String index(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
        Page<ProductResponseDTO> productsPage = productService.getAllProducts(page, 10);
        model.addAttribute("products", productsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("hasNextPage", productsPage.hasNext());
        return "index";
    }

    @GetMapping("/products")
    public String products(@RequestParam(value = "page", defaultValue = "0") int page,
                           @RequestParam(value = "searchQuery", defaultValue = "") String searchQuery,
                           Model model) {
        Page<ProductResponseDTO> productsPage = productService.getAllProducts(page, 10);
        List<ProductResponseDTO> products = productService.getAllProducts(searchQuery);
        model.addAttribute("products", products);
        model.addAttribute("currentPage", page);
        model.addAttribute("hasNextPage", products.size() > (page + 1) * 10);
        model.addAttribute("searchQuery", searchQuery);
        return "index";
    }
}