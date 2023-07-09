package com.fid3lles.marketsys.controllers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fid3lles.marketsys.dtos.ProductRecordDTO;
import com.fid3lles.marketsys.models.Product;
import com.fid3lles.marketsys.repositories.ProductRepository;

import jakarta.validation.Valid;

@RestController
public class ProductController {
    
    @Autowired
    ProductRepository productRepository;

    @PostMapping("/products")
    public ResponseEntity<Product> saveProduct(@RequestBody @Valid ProductRecordDTO productRecordDto){
        var product = new Product();
        BeanUtils.copyProperties(productRecordDto, product);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productRepository.save(product));
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts(){
        List<Product> productsList = productRepository.findAll();

        if(!productsList.isEmpty()){
            for(Product product : productsList){
                UUID id = product.getId();
                product.add(linkTo(methodOn(ProductController.class).getProduct(id)).withSelfRel());
            }
        }

        return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(productsList);
    }
    
    @GetMapping("/products/{id}")
    public ResponseEntity<Object> getProduct(@PathVariable(value = "id") UUID id){
        Optional<Product> productOpt = productRepository.findById(id);

        if(productOpt.isEmpty()){
            return ResponseEntity
                        .status(HttpStatus.NO_CONTENT)
                        .body("Product not found.");
        }

        productOpt.get().add(linkTo(methodOn(ProductController.class).getAllProducts()).withSelfRel());

        return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(productOpt.get());
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Object> updateProduct(@PathVariable(value = "id") UUID id, 
                                                    @RequestBody @Valid ProductRecordDTO productRecordDTO){
        Optional<Product> productOpt = productRepository.findById(id);

        if(productOpt.isEmpty()){
            return ResponseEntity
                        .status(HttpStatus.NO_CONTENT)
                        .body("Product not found.");
        }

        var product = productOpt.get();
        BeanUtils.copyProperties(productRecordDTO, product);

        return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(productRepository.save(product));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Object> deleteProduct(@PathVariable(value = "id") UUID id){

        Optional<Product> productOpt = productRepository.findById(id);

        if(productOpt.isEmpty()){
            return ResponseEntity
                        .status(HttpStatus.NO_CONTENT)
                        .body("Product not found.");
        }
        
        productRepository.delete(productOpt.get());
        return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Product deleted succefully.");
    }
}