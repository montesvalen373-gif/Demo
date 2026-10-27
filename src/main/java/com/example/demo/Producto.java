package com.example.demo;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class Producto {

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private String id;

	@NotBlank
	@Size(max = 100)
	private String nombre;

	@NotNull
	@Min(0)
	private Double precio;

	@NotNull
	@Min(0)
	@Max(1000000)
	private Double costo;

	public Producto() {
		this.id = UUID.randomUUID().toString();
	}

	public Producto(String nombre, Double precio, Double costo) {
		this();
		this.nombre = nombre;
		this.precio = precio;
		this.costo = costo;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Double getPrecio() {
		return precio;
	}

	public void setPrecio(Double precio) {
		this.precio = precio;
	}

	public Double getCosto() {
		return costo;
	}

	public void setCosto(Double costo) {
		this.costo = costo;
	}
}
