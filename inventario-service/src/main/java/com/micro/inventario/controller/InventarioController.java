package com.micro.inventario.controller;

import com.micro.inventario.entity.Inventario;
import com.micro.inventario.service.InventarioService;

import io.swagger.v3.oas.annotations.Operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/inventarios")
public class InventarioController {

	private static final Logger log = LoggerFactory.getLogger(InventarioController.class);

	@Autowired
	private InventarioService service;

	@Value("${api.key}")
	private String apiKey;

	private void validarApiKey(String headerKey) {
		if (!apiKey.equals(headerKey)) {
			log.warn("Intento de acceso con API Key inválida: {}", headerKey);
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
		}
	}
	
    @Operation(summary = "Mostrar en pantalla un producto dependiendo de su ID", description = "Devuelve una producto realizando busqueda por ID.")
	@GetMapping("/obtener/{productoId}")
	public ResponseEntity<?> consultar(@RequestHeader("x-api-key") String key, @PathVariable("productoId") Long productoId) {
		try {
			validarApiKey(key);
			log.info("Consultando inventario para producto ID: {}", productoId);

			Inventario i = service.consultar(productoId);
			return ResponseEntity.ok(jsonApiWrapper("inventarios", i.getId(), i));
		} catch (ResponseStatusException e) {
			return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
		} catch (NoSuchElementException e) {
			log.warn("Inventario no encontrado para producto ID: {}", productoId);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Inventario no encontrado.");
		} catch (Exception e) {
			log.error("Error al consultar inventario para producto ID: {}", productoId, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error al consultar el inventario.");
		}
	}

    @Operation(summary = "Crear y/o actualizar el inventario de un producto por ID", description = "Busca en base de datos si el inventario del producto ingresado existe, en caso afirmativo lo actualiza, en caso negativo lo crea.")
	@PutMapping("/crearActualizar/{productoId}")
	public ResponseEntity<?> actualizar(@RequestHeader("x-api-key") String key,
			@PathVariable("productoId") Long productoId,
			@RequestParam(name = "cantidad") int cantidad) {
		try {
			validarApiKey(key);
			log.info("Actualizando inventario del producto ID {} con cantidad: {}", productoId, cantidad);

			Inventario i = service.actualizarCantidad(productoId, cantidad);
			return ResponseEntity.ok(jsonApiWrapper("inventarios", i.getId(), i));
		} catch (ResponseStatusException e) {
			return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
		} catch (NoSuchElementException e) {
			log.warn("No se encontró inventario para actualizar, producto ID: {}", productoId);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Inventario no encontrado.");
		} catch (IllegalArgumentException e) {
			log.warn("Cantidad inválida para producto ID {}: {}", productoId, cantidad);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (Exception e) {
			log.error("Error al actualizar inventario para producto ID: {}", productoId, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al actualizar inventario.");
		}
	}
    
    @Operation(summary = "Realiza venta de productos por ID y actualiza la cantidad en inventario", description = "Busca en base de datos si el inventario del producto ingresado existe, en caso afirmativo lo resta dependiendo la cantidad de venta, en caso negativo indica que no hay producto disponible")
	@PutMapping("/vender/{productoId}")
	public ResponseEntity<?> venderProducto(@PathVariable("productoId") Long productoId,
			@RequestParam(name = "cantidadVendida") int cantidadVendida,
			@RequestHeader("x-api-key") String key) {
		try {
			validarApiKey(key);
			log.info("Procesando venta - producto ID: {}, cantidad vendida: {}", productoId, cantidadVendida);

			Inventario actualizado = service.venderProducto(productoId, cantidadVendida);
			return ResponseEntity.ok(jsonApiWrapper("inventarios", actualizado.getId(), actualizado));
		} catch (ResponseStatusException e) {
			return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
		} catch (IllegalArgumentException e) {
			log.warn("Error de negocio al vender producto ID {}: {}", productoId, e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (NoSuchElementException e) {
			log.warn("Producto no encontrado al vender, ID: {}", productoId);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Inventario no encontrado.");
		} catch (Exception e) {
			log.error("Error inesperado al vender producto ID: {}", productoId, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al procesar la venta.");
		}
	}

	private Map<String, Object> jsonApiWrapper(String type, Long id, Inventario i) {
		return Map.of(
				"data", Map.of(
						"type", type,
						"id", id,
						"attributes", Map.of(
								"productoId", i.getProductoId(),
								"cantidad", i.getCantidad()
								)
						)
				);
	}
}
