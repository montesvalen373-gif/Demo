package com.example.demo;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class ProductoService {

	private final ProductoRepository productoRepository;

	public ProductoService(ProductoRepository productoRepository) {
		this.productoRepository = productoRepository;
		initSampleData();
	}

	private void initSampleData() {
		save(new Producto("Chispas", 1400.0, 6000.0));
		save(new Producto("Mini chips", 1000.0, 4000.0));
		save(new Producto("Churros", 2000.0, 6000.0));
		save(new Producto("merenges", 1000.0, 3000.0));
		save(new Producto("cheesecake", 8000.0, 20000.0));

	}

	public Producto save(Producto producto) {
		return productoRepository.save(producto);
	}

	public Producto findById(String id) {
		return productoRepository.findById(id);
	}

	public List<Producto> findAll() {
		return productoRepository.findAll();
	}

	public List<Producto> findByNombre(String nombre) {
		return productoRepository.findByNombreContaining(nombre);
	}

	public Producto update(Producto producto) {
		return productoRepository.update(producto);
	}

	public Producto patch(String id, Map<String, Object> updates) {
		Producto existingProducto = productoRepository.findById(id);
		if (existingProducto == null) {
			return null;
		}

		Producto producto = new Producto(existingProducto.getNombre(), existingProducto.getCosto(), existingProducto.getPrecio());
		producto.setId(existingProducto.getId());

		updates.forEach((key, value) -> {
			switch (key) {
				case "nombre" -> producto.setNombre(asString(key, value));
				case "costo" -> producto.setCosto(asDouble(key, value));
				case "precio" -> producto.setPrecio(asDouble(key, value));
				default -> {
				}
			}
		});
		return productoRepository.update(producto);
	}

	private String asString(String field, Object value) {
		if (value != null && !(value instanceof String)) {
			throw new IllegalArgumentException("El campo '" + field + "' debe ser texto.");
		}
		return (String) value;
	}

	private double asDouble(String field, Object value) {
		if (!(value instanceof Number number)) {
			throw new IllegalArgumentException("El campo '" + field + "' debe ser un número.");
		}
		return number.doubleValue();
	}

	public void deleteById(String id) {
		productoRepository.deleteById(id);
	}
}