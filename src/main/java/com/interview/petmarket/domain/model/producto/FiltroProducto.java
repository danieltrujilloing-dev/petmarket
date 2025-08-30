package com.interview.petmarket.domain.model.producto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Objeto de valor que encapsula los filtros para búsqueda de productos usando Lombok.
 */
@Data
@RequiredArgsConstructor
@Builder
public class FiltroProducto {
    
    private final Set<TipoProducto> tipos;
    private final Set<EspecieAnimal> especies;
    private final BigDecimal precioMinimo;
    private final BigDecimal precioMaximo;
    private final String textoBusqueda;
    private final Boolean soloActivos;
    private final Boolean soloDisponibles;

    /**
     * Verifica si el filtro tiene al menos un criterio definido.
     */
    public boolean tieneAlgunFiltro() {
        return (tipos != null && !tipos.isEmpty()) ||
               (especies != null && !especies.isEmpty()) ||
               precioMinimo != null ||
               precioMaximo != null ||
               (textoBusqueda != null && !textoBusqueda.trim().isEmpty()) ||
               soloActivos != null ||
               soloDisponibles != null;
    }

    /**
     * Verifica si el filtro de precio está configurado.
     */
    public boolean tieneFiltroPrecios() {
        return precioMinimo != null || precioMaximo != null;
    }

    /**
     * Genera una clave única para el cache basada en los filtros.
     */
    public String generarClaveCache() {
        StringBuilder clave = new StringBuilder("productos:filtro:");
        
        if (tipos != null && !tipos.isEmpty()) {
            clave.append("tipos:").append(String.join(",", 
                tipos.stream().map(Enum::name).sorted().toArray(String[]::new)));
        }
        
        if (especies != null && !especies.isEmpty()) {
            clave.append(":especies:").append(String.join(",", 
                especies.stream().map(Enum::name).sorted().toArray(String[]::new)));
        }
        
        if (precioMinimo != null) {
            clave.append(":min:").append(precioMinimo);
        }
        
        if (precioMaximo != null) {
            clave.append(":max:").append(precioMaximo);
        }
        
        if (textoBusqueda != null && !textoBusqueda.trim().isEmpty()) {
            // Normalizar texto para cache
            String textoNormalizado = textoBusqueda.trim().toLowerCase().replaceAll("\\s+", "_");
            clave.append(":texto:").append(textoNormalizado);
        }
        
        if (soloActivos != null) {
            clave.append(":activos:").append(soloActivos);
        }
        
        if (soloDisponibles != null) {
            clave.append(":disponibles:").append(soloDisponibles);
        }
        
        return clave.toString();
    }

    /**
     * Validaciones personalizadas para el builder.
     */
    public static class FiltroProductoBuilder {
        public FiltroProductoBuilder precioMinimo(BigDecimal precioMinimo) {
            if (precioMinimo != null && precioMinimo.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Minimum price cannot be negative");
            }
            this.precioMinimo = precioMinimo;
            return this;
        }

        public FiltroProductoBuilder precioMaximo(BigDecimal precioMaximo) {
            if (precioMaximo != null && precioMaximo.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Maximum price must be greater than zero");
            }
            this.precioMaximo = precioMaximo;
            return this;
        }

        public FiltroProducto build() {
            // Validación de rango de precios
            if (precioMinimo != null && precioMaximo != null && 
                precioMinimo.compareTo(precioMaximo) > 0) {
                throw new IllegalArgumentException("Minimum price cannot be greater than maximum price");
            }
            
            return new FiltroProducto(tipos, especies, precioMinimo, precioMaximo, 
                                    textoBusqueda, soloActivos, soloDisponibles);
        }
    }

    @Override
    public String toString() {
        return String.format("FiltroProducto{tipos=%s, especies=%s, precioMin=%s, precioMax=%s, texto='%s', activos=%s, disponibles=%s}",
                tipos, especies, precioMinimo, precioMaximo, textoBusqueda, soloActivos, soloDisponibles);
    }
}
