package guru.sfg.beer.order.service.services;

import guru.sfg.beer.order.service.domain.BeerOrder;
import guru.sfg.beer.order.service.domain.BeerOrderEvent;
import guru.sfg.beer.order.service.domain.BeerOrderStatus;
import guru.sfg.beer.order.service.repositories.BeerOrderRepository;
import guru.sfg.beer.order.service.state.machine.BeerOrderStateChangeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class BeerOrderManagerImpl implements BeerOrderManager {

    public static final String ORDER_ID_HEADER ="ORDER_ID_HEADER";
    private final StateMachineFactory<BeerOrderStatus, BeerOrderEvent> stateMachineFactory;
    private final BeerOrderRepository repository;
    private final BeerOrderStateChangeInterceptor interceptor;

    @Transactional
    @Override
    public BeerOrder newBeerOrder(BeerOrder beerOrder) {
        beerOrder.setId(null);
        beerOrder.setOrderStatus(BeerOrderStatus.NEW);
        BeerOrder savedBeerOrder = repository.save(beerOrder);
        sendBeerOrderEvent(savedBeerOrder);
        return savedBeerOrder;
    }

    private void sendBeerOrderEvent(BeerOrder beerOrder) {
        StateMachine<BeerOrderStatus, BeerOrderEvent> sm = build(beerOrder);
        Message<BeerOrderEvent> msg = MessageBuilder.withPayload(BeerOrderEvent.VALIDATE_ORDER)
                .setHeader(ORDER_ID_HEADER, beerOrder.getId().toString())
                .build();
        Mono<Message<BeerOrderEvent>> messageMono = Mono.just(msg);
        sm.sendEvent(messageMono);
    }

    private StateMachine<BeerOrderStatus, BeerOrderEvent> build(BeerOrder beerOrder) {
        StateMachine<BeerOrderStatus, BeerOrderEvent> sm = stateMachineFactory.getStateMachine(beerOrder.getId());
        sm.stopReactively();
        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.addStateMachineInterceptor(interceptor);
                    sma.resetStateMachineReactively(
                            new DefaultStateMachineContext<>(beerOrder.getOrderStatus(), null, null, null));
                });
        sm.startReactively();
        return sm;
    }
}