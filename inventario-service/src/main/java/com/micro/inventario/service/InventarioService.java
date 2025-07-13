package com.micro.inventario.service;

import com.micro.inventario.dto.ProductoDTO;
import com.micro.inventario.entity.Inventario;
import com.micro.inventario.repository.InventarioRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class InventarioService {

	private static final Logger log = LoggerFactory.getLogger(InventarioService.class);

	@Autowired
	private InventarioRepository repository;

	@Autowired
	private RestTemplate restTemplate;

	@Value("${productos.url}")
	private String productosUrl;

	@Value("${api.key}")
	private String apiKey;

	public ProductoDTO obtenerProducto(Long productoId) {
	    log.debug("Consultando producto con ID: {}", productoId);

	    HttpHeaders headers = new HttpHeaders();
	    headers.set("x-api-key", apiKey);
	    HttpEntity<Void> entity = new HttpEntity<>(headers);

	    int maxIntentos = 3;
	    int intentos = 0;

	    while (intentos < maxIntentos) {
	        try {
	            ResponseEntity<Map> response = restTemplate.exchange(
	                    productosUrl + "/" + productoId,
	                    HttpMethod.GET,
	                    entity,
	                    Map.class
	            );

	            if (!response.getStatusCode().is2xxSuccessful()) {
	                log.error("Código HTTP inesperado al consultar producto ID {}: {}", productoId, response.getStatusCode());
	                throw new RuntimeException("Respuesta inválida del servicio de productos");
	            }

	            Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
	            Map<String, Object> attributes = (Map<String, Object>) data.get("attributes");

	            ProductoDTO dto = new ProductoDTO();
	            dto.setId(Long.valueOf(data.get("id").toString()));
	            dto.setNombre((String) attributes.get("nombre"));
	            dto.setPrecio(Double.valueOf(attributes.get("precio").toString()));

	            log.info("Producto consultado exitosamente: ID {}, nombre {}", dto.getId(), dto.getNombre());
	            return dto;

	        } catch (Exception e) {
	            intentos++;
	            log.warn("Fallo al consultar producto ID {}. Intento {}/{}. Error: {}", productoId, intentos, maxIntentos, e.getMessage());

	            if (intentos >= maxIntentos) {
	                log.error("❌ No se pudo obtener el producto ID {} después de {} intentos", productoId, maxIntentos);
	                throw new RuntimeException("No se pudo obtener el producto tras varios intentos", e);
	            }

	            try {
	                Thread.sleep(1000); // Espera 1 segundo entre intentos
	            } catch (InterruptedException ie) {
	                Thread.currentThread().interrupt();
	                throw new RuntimeException("Reintento interrumpido", ie);
	            }
	        }
	    }

	    throw new RuntimeException("Error inesperado en la consulta de producto");
	}
	public Inventario consultar(Long productoId) {
		log.info("Consultando inventario para producto ID: {}", productoId);
		obtenerProducto(productoId); // valida existencia del producto

		return repository.findByProductoId(productoId)
				.orElseThrow(() -> {
					log.warn("Inventario no encontrado para producto ID: {}", productoId);
					return new NoSuchElementException("Inventario no encontrado");
				});
	}

	public Inventario venderProducto(Long productoId, int cantidadVendida) {
		log.info("Procesando venta - producto ID: {}, cantidad vendida: {}", productoId, cantidadVendida);

		ProductoDTO producto = obtenerProducto(productoId);
		if (producto == null) {
			log.warn("Producto no encontrado para venta. ID: {}", productoId);
			throw new NoSuchElementException("Producto no encontrado");
		}

		Inventario inventario = repository.findByProductoId(productoId)
				.orElseThrow(() -> {
					log.warn("Inventario no encontrado para producto ID: {}", productoId);
					return new NoSuchElementException("No hay inventario para este producto");
				});

		if (inventario.getCantidad() < cantidadVendida) {
			log.warn("Stock insuficiente - producto ID: {}, solicitado: {}, disponible: {}",
					productoId, cantidadVendida, inventario.getCantidad());
			throw new IllegalArgumentException("No hay stock suficiente");
		}

		inventario.setCantidad(inventario.getCantidad() - cantidadVendida);
		Inventario guardado = repository.save(inventario);

		log.info("Venta registrada - producto ID: {}, cantidad restante: {}",
				productoId, guardado.getCantidad());

		return guardado;
	}

	public Inventario actualizarCantidad(Long productoId, int nuevaCantidad) {
		log.info("Actualizando cantidad de inventario - producto ID: {}, nueva cantidad: {}", productoId, nuevaCantidad);

		obtenerProducto(productoId); // valida existencia del producto

		Inventario inventario = repository.findByProductoId(productoId)
				.orElse(new Inventario(null, productoId, 0));

		inventario.setCantidad(nuevaCantidad);
		Inventario actualizado = repository.save(inventario);

		log.info("Inventario actualizado exitosamente - producto ID: {}, cantidad actual: {}", productoId, nuevaCantidad);
		return actualizado;
	}
}