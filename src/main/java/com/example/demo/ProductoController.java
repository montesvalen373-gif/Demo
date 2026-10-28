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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "API para la gestión de productos")
public class ProductoController {

	private final ProductoService productoService;

	public ProductoController(ProductoService productoService) {
		this.productoService = productoService;
	}

	@GetMapping
	@Operation(summary = "Obtener todos los productos", description = "Devuelve una lista de todos los productos registrados.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Lista de productos obtenida con éxito"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	public ResponseEntity<List<Producto>> getAllProductos() {
		return ResponseEntity.ok(productoService.findAll());
	}

	@GetMapping("/buscar")
	@Operation(summary = "Buscar productos por nombre", description = "Devuelve una lista de productos que coinciden con el nombre proporcionado.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Lista de productos obtenida con éxito"),
			@ApiResponse(responseCode = "404", description = "Producto no encontrado")
	})
	public ResponseEntity<List<Producto>> getProductosByQuery(@RequestParam @Parameter(description = "Nombre del producto") String nombre) {
		return ResponseEntity.ok(productoService.findByNombre(nombre));
	}

	@GetMapping("/cabecera")
	@Operation(summary = "Obtener información del producto", description = "Devuelve información sobre el producto basada en el encabezado de solicitud.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Información del producto obtenida con éxito")
	})
	public ResponseEntity<String> getAgentInfo(
			@RequestHeader("producto-Agent") @Parameter(description = "Encabezado con la información del agente/producto") String userAgent) {
		return ResponseEntity.ok("Información del producto (producto-Agent): " + userAgent);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Obtener producto por ID", description = "Devuelve la información de un producto específico basado en su ID.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Producto obtenido con éxito"),
			@ApiResponse(responseCode = "404", description = "Producto no encontrado")
	})
	public ResponseEntity<Producto> getProductoById(
			@PathVariable @Parameter(description = "ID del producto a buscar") String id) {
		Producto producto = productoService.findById(id);
		return producto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(producto);
	}

	@PostMapping
	@Operation(summary = "Crear nuevo producto", description = "Crea un nuevo producto con la información proporcionada.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Producto creado con éxito"),
			@ApiResponse(responseCode = "400", description = "Solicitud inválida")
	})
	public ResponseEntity<Producto> createProducto(
			@RequestBody @Parameter(description = "Datos del producto a crear") Producto producto) {
		Producto newProducto = productoService.save(producto);
		return ResponseEntity.status(HttpStatus.CREATED).body(newProducto);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Actualizar producto", description = "Actualiza la información de un producto específico basado en su ID.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Producto actualizado con éxito"),
			@ApiResponse(responseCode = "404", description = "Producto no encontrado")
	})
	public ResponseEntity<Producto> updateProducto(
			@PathVariable @Parameter(description = "ID del producto a actualizar") String id,
			@RequestBody @Parameter(description = "Nuevos datos del producto") Producto producto) {
		producto.setId(id);
		Producto updatedProducto = productoService.update(producto);
		return updatedProducto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(updatedProducto);
	}

	@PatchMapping("/{id}")
	@Operation(summary = "Actualizar parcialmente un producto", description = "Actualiza parcialmente la información de un producto específico basado en su ID.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Producto actualizado con éxito"),
			@ApiResponse(responseCode = "400", description = "Parámetros de actualización inválidos"),
			@ApiResponse(responseCode = "404", description = "Producto no encontrado")
	})
	public ResponseEntity<?> patchUpdateProducto(
			@PathVariable @Parameter(description = "ID del producto a modificar parcialmente") String id,
			@RequestBody @Parameter(description = "Campos a actualizar en formato clave-valor") Map<String, Object> updates) {
		try {
			Producto updatedProducto = productoService.patch(id, updates);
			return updatedProducto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(updatedProducto);
		} catch (IllegalArgumentException exception) {
			return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
		}
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Eliminar producto", description = "Elimina un producto registrado mediante su ID.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Producto eliminado con éxito"),
			@ApiResponse(responseCode = "404", description = "Producto no encontrado")
	})
	public ResponseEntity<Void> deleteProducto(
			@PathVariable @Parameter(description = "ID del producto a eliminar") String id) {
		if (productoService.findById(id) == null) {
			return ResponseEntity.notFound().build();
		}
		productoService.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}