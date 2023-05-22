package guru.sfg.beer.order.service.services.listeners;

import guru.sfg.beer.order.service.config.JmsConfig;
import guru.sfg.beer.order.service.services.BeerOrderManager;
import guru.sfg.brewery.model.events.AllocateOrderResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AllocationResultListener {
    private final BeerOrderManager manager;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE)
    public void listener(AllocateOrderResult result) {
        final UUID beerOrderId = result.getBeerOrderDto().getId();
        log.debug("Allocation Result for order id: " + beerOrderId);
        if (result.getAllocationError())
            manager.beerOrderAllocationFailed(result.getBeerOrderDto());
        else if (result.getPendingInventory())
            manager.beerOrderAllocationPendingInventory(result.getBeerOrderDto());
        else
            manager.beerOrderAllocationPassed(result.getBeerOrderDto());
    }

}
