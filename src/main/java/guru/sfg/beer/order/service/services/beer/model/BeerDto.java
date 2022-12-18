package guru.sfg.beer.order.service.services.beer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeerDto {
    private String beerName;
    private String beerStyle;
    private BigDecimal price;
}
