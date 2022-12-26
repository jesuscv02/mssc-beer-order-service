package guru.sfg.beer.order.service.services.beer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeerDto {
    private Long id;
    private String beerName;
    private String beerStyle;
    private BigDecimal price;
}
