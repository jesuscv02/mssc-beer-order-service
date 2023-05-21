package guru.sfg.beer.order.service.state.machine;

import guru.sfg.beer.order.service.domain.BeerOrder;
import guru.sfg.beer.order.service.domain.BeerOrderEvent;
import guru.sfg.beer.order.service.domain.BeerOrderStatus;
import guru.sfg.beer.order.service.repositories.BeerOrderRepository;
import guru.sfg.beer.order.service.services.BeerOrderManagerImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BeerOrderStateChangeInterceptor extends StateMachineInterceptorAdapter<BeerOrderStatus, BeerOrderEvent> {
    private final BeerOrderRepository repository;

    @Override
    public void preStateChange(State<BeerOrderStatus, BeerOrderEvent> state, Message<BeerOrderEvent> message,
                               Transition<BeerOrderStatus, BeerOrderEvent> transition,
                               StateMachine<BeerOrderStatus, BeerOrderEvent> stateMachine,
                               StateMachine<BeerOrderStatus, BeerOrderEvent> stateMachine1) {
        Optional.ofNullable(message).flatMap(msg ->
                        Optional.ofNullable(msg.getHeaders().getOrDefault(BeerOrderManagerImpl.ORDER_ID_HEADER, " ")))
                .ifPresent(beerOrderId -> {
                    log.debug("Saving state for beer order id: " + beerOrderId + "with status: " + state.getId());
                    BeerOrder beerOrder = repository.findById((UUID) beerOrderId)
                            .orElseThrow(() -> new RuntimeException("There was no state machine with id: " + beerOrderId));
                    beerOrder.setOrderStatus(state.getId());
                    repository.saveAndFlush(beerOrder);
                });
    }
}
