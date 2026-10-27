package com.example.demo;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

	private final ProductoService  productoService;

	public ProductoController(ProductoService productoService) {
		this.productoService = productoService;
	}

	@GetMapping
	public ResponseEntity<List<Producto>> getAllProductos() {
		return ResponseEntity.ok(productoService.findAll());
	}

	@GetMapping("/buscar")
	public ResponseEntity<List<Producto>> getProductosByQuery(@RequestParam String nombre) {
		return ResponseEntity.ok(productoService.findByNombre(nombre));
	}

	@GetMapping("/cabecera")
	public ResponseEntity<String> getAgentInfo(@RequestHeader("User-Agent") String userAgent) {
		return ResponseEntity.ok("Información del cliente (User-Agent): " + userAgent);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Producto> getProductoById(@PathVariable String id) {
		Producto producto = productoService.findById(id);
		return producto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(producto);
	}

	@PostMapping
	public ResponseEntity<Producto> createProducto(@RequestBody Producto producto) {
		Producto newProducto = productoService.save(producto);
		return ResponseEntity.status(HttpStatus.CREATED).body(newProducto);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Producto> updateProducto(@PathVariable String id, @RequestBody Producto producto) {
		producto.setId(id);
		Producto updatedProducto = productoService.update(producto);
		return updatedProducto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(updatedProducto);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<?> patchUpdateProducto(@PathVariable String id,
	                                             @RequestBody Map<String, Object> updates) {
		try {
			Producto updatedProducto = productoService.patch(id, updates);
			return updatedProducto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(updatedProducto);
		} catch (IllegalArgumentException exception) {
			return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteProducto(@PathVariable String id) {
		if (productoService.findById(id) == null) {
			return ResponseEntity.notFound().build();
		}
		productoService.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}