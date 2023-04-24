package guru.sfg.beer.order.service.state.machine;

import guru.sfg.beer.order.service.domain.BeerOrderEven;
import guru.sfg.beer.order.service.domain.BeerOrderStatus;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory
public class BeerOrderStateMachineConfig extends StateMachineConfigurerAdapter<BeerOrderStatus, BeerOrderEven> {
    @Override
    public void configure(StateMachineStateConfigurer<BeerOrderStatus, BeerOrderEven> states) throws Exception {
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
    public void configure(StateMachineTransitionConfigurer<BeerOrderStatus, BeerOrderEven> transitions) throws Exception {
        transitions.withExternal().source(BeerOrderStatus.NEW).target(BeerOrderStatus.NEW)
                .event(BeerOrderEven.VALIDATE_ORDER)
                .and()
                .withExternal().source(BeerOrderStatus.NEW).target(BeerOrderStatus.VALIDATED)
                .event(BeerOrderEven.VALIDATION_PASS)
                .and()
                .withExternal().source(BeerOrderStatus.NEW).target(BeerOrderStatus.VALIDATION_EXCEPTION)
                .event(BeerOrderEven.VALIDATION_FAIL);
    }
}
