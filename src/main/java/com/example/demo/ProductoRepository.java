package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Repository;

@Repository
public class ProductoRepository {

	private final ConcurrentMap<String, Producto> baseDeDatos = new ConcurrentHashMap<>();

	public Producto save(Producto producto) {
		baseDeDatos.put(producto.getId(), producto);
		return producto;
	}

	public Producto findById(String id) {
		return baseDeDatos.get(id);
	}

	public List<Producto> findAll() {
		return new ArrayList<>(baseDeDatos.values());
	}

	public List<Producto> findByNombreContaining(String nombre) {
		String consulta = nombre.toLowerCase(Locale.ROOT);
		return baseDeDatos.values().stream()
				.filter(producto -> producto.getNombre() != null
						&& producto.getNombre().toLowerCase(Locale.ROOT).contains(consulta))
				.toList();
	}

	public void deleteById(String id) {
		baseDeDatos.remove(id);
	}

	public Producto update(Producto producto) {
		if (!baseDeDatos.containsKey(producto.getId())) {
			return null;
		}
		baseDeDatos.put(producto.getId(), producto);
		return producto;
	}
}