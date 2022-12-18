package guru.sfg.beer.order.service.services.beer;

import guru.sfg.beer.order.service.services.beer.model.BeerDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@ConfigurationProperties(prefix = "sfg.brewery", ignoreUnknownFields = false)
@Service
public class BeerServiceRestTemplateImpl implements BeerService {

    private final String BEER_UPC_PATH = "/api/v1/beerUPC/";
    private final String BEER_ID_PATH = "/api/v1/beer/";
    private final RestTemplate restTemplate;
    private String beerServiceHost;

    public BeerServiceRestTemplateImpl(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public void setBeerServiceHost(String beerServiceHost) {
        this.beerServiceHost = beerServiceHost;
    }

    @Override
    public Optional<BeerDto> getBeerUpc(String upc) {
        log.debug("Calling Beer Service");
        log.info(beerServiceHost + BEER_UPC_PATH);
        return Optional.ofNullable(restTemplate.getForObject(beerServiceHost + BEER_UPC_PATH + upc, BeerDto.class));
    }

    @Override
    public Optional<BeerDto> getBeerById(UUID uuid) {
        log.debug("Calling Beer Service");
        log.info(beerServiceHost + BEER_ID_PATH);
        return Optional.ofNullable(restTemplate.getForObject(beerServiceHost + BEER_ID_PATH + uuid.toString(), BeerDto.class));
    }

}
