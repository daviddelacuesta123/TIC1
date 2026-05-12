package com.logistica.logistica_urbana.presentation.controller;

import com.logistica.logistica_urbana.infrastructure.persistence.entity.ProductoJpaEntity;
import com.logistica.logistica_urbana.infrastructure.persistence.repository.ProductoJpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoJpaRepository productoRepo;

    public ProductoController(ProductoJpaRepository productoRepo) {
        this.productoRepo = productoRepo;
    }

    @GetMapping
    public ResponseEntity<List<ProductoJpaEntity>> listar() {
        return ResponseEntity.ok(productoRepo.findAll());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ProductoJpaEntity producto) {
        if (producto.getNombre() == null || producto.getNombre().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El nombre es requerido"));
        }
        if (producto.getPeso() == null || producto.getPeso() < 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "El peso debe ser mayor o igual a 0"));
        }
        if (producto.getVolumen() == null || producto.getVolumen() < 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "El volumen debe ser mayor o igual a 0"));
        }
        producto.setId(null);
        ProductoJpaEntity guardado = productoRepo.save(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!productoRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productoRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
