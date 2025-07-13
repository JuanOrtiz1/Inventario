package com.micro.inventario.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_inventario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventario {
	
	
	
	public Inventario() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Inventario(Long id, Long productoId, int cantidad) {
		super();
		this.id = id;
		this.productoId = productoId;
		this.cantidad = cantidad;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
    @Column(nullable = false)
	private Long productoId;
    
    @Column(nullable = false)	
    private int cantidad;
	
    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getProductoId() {
		return productoId;
	}
	public void setProductoId(Long productoId) {
		this.productoId = productoId;
	}
	public int getCantidad() {
		return cantidad;
	}
	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}
}
