package guru.sfg.beer.order.service.state.machine;

import guru.sfg.beer.order.service.domain.BeerOrderEvent;
import guru.sfg.beer.order.service.domain.BeerOrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@RequiredArgsConstructor
@EnableStateMachineFactory
public class BeerOrderStateMachineConfig extends StateMachineConfigurerAdapter<BeerOrderStatus, BeerOrderEvent> {
    private final Action<BeerOrderStatus, BeerOrderEvent> validateBeerOrderAction;
    private final Action<BeerOrderStatus, BeerOrderEvent> allocationOrderAction;

    @Override
    public void configure(StateMachineStateConfigurer<BeerOrderStatus, BeerOrderEvent> states) throws Exception {
        states.withStates()
                .initial(BeerOrderStatus.NEW)
                .states(EnumSet.allOf(BeerOrderStatus.class))
                .end(BeerOrderStatus.VALIDATION_EXCEPTION)
                .end(BeerOrderStatus.ALLOCATION_EXCEPTION)
                .end(BeerOrderStatus.DELIVERY_EXCEPTION)
                .end(BeerOrderStatus.PICKED_UP)
                .end(BeerOrderStatus.DELIVERED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<BeerOrderStatus, BeerOrderEvent> transitions) throws Exception {
        transitions.withExternal().source(BeerOrderStatus.NEW).target(BeerOrderStatus.VALIDATION_PENDING)
                .event(BeerOrderEvent.VALIDATE_ORDER)
                .action(validateBeerOrderAction)
                .and()
                .withExternal().source(BeerOrderStatus.NEW).target(BeerOrderStatus.VALIDATED)
                .event(BeerOrderEvent.VALIDATION_PASSED)
                .and()
                .withExternal().source(BeerOrderStatus.NEW).target(BeerOrderStatus.VALIDATION_EXCEPTION)
                .event(BeerOrderEvent.VALIDATION_FAILED)
                .and()
                .withExternal().source(BeerOrderStatus.VALIDATED).target(BeerOrderStatus.ALLOCATION_PENDING)
                .event(BeerOrderEvent.ALLOCATE_ORDER)
                .action(allocationOrderAction)
                .and()
                .withExternal().source(BeerOrderStatus.ALLOCATION_PENDING).target(BeerOrderStatus.ALLOCATED)
                .event(BeerOrderEvent.ALLOCATION_SUCCESS)
                .and()
                .withExternal().source(BeerOrderStatus.ALLOCATION_PENDING).target(BeerOrderStatus.ALLOCATION_EXCEPTION)
                .event(BeerOrderEvent.ALLOCATION_FAILED)
                .and()
                .withExternal().source(BeerOrderStatus.ALLOCATION_PENDING).source(BeerOrderStatus.PENDING_INVENTORY)
                .event(BeerOrderEvent.ALLOCATION_NO_INVENTORY);
    }
}
