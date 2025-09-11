package co.com.pragma.model.solicitud;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminFilters {

    private String emailLike;
    private Long tipoPrestamoId;
    private BigDecimal minMonto;
    private BigDecimal maxMonto;
    private Integer minPlazo;
    private Integer maxPlazo;
}
