package com.micro.inventario.dto;

import lombok.Data;

@Data
public class ProductoDTO {
	
	public ProductoDTO(Long id, String nombre, Double precio) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.precio = precio;
	}

	
	
	public ProductoDTO() {
		super();
		// TODO Auto-generated constructor stub
	}



	private Long id;
	private String nombre;
	private Double precio;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
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
}