package com.astrog.shootergame.server.internal;

import com.astrog.shootergame.common.messaging.CustomEvent;
import com.astrog.shootergame.common.messaging.Event;
import com.astrog.shootergame.server.domain.Dispatcher;
import com.astrog.shootergame.server.domain.RestController;
import com.astrog.shootergame.server.domain.ServerState;
import com.astrog.shootergame.server.domain.model.Message;

import java.util.ArrayList;
import java.util.List;


public class ShooterGameRestController extends RestController {

    public static ShooterGameRestController currentController;
    private final List<Dispatcher> dispatcherChain = new ArrayList<>();
    private ServerState state;

    public ShooterGameRestController(ServerState initialState) {
        this.state = initialState;
        state.onStart();
        initDispatcherChain();
    }

    public static void tryUpdateStateIfEnded() {
        currentController.updateStateIfEnded();
    }

    @Override
    protected List<Dispatcher> getDispatchers() {
        return dispatcherChain;
    }

    private void updateStateIfEnded() {
        if (state.isEnded()) {
            state.onEnd();
            state = state.getNextState();
            state.onStart();
            System.out.println("State updated");
        }
    }

    private void initDispatcherChain() {
        dispatcherChain.add(new Dispatcher(Event.CONNECTION.name()) {
            @Override
            protected boolean dispatch(Message message) {
                updateStateIfEnded();
                state.onConnection(message.from());
                updateStateIfEnded();
                return false;
            }
        });
        dispatcherChain.add(new Dispatcher(CustomEvent.SHOOT.name()) {
            @Override
            protected boolean dispatch(Message message) {
                updateStateIfEnded();
                state.onShoot(message.from());
                updateStateIfEnded();
                return false;
            }
        });
        dispatcherChain.add(new Dispatcher(CustomEvent.LOGIN.name()) {
            @Override
            protected boolean dispatch(Message message) {
                updateStateIfEnded();
                state.onLogin(message.from(), message.args());
                updateStateIfEnded();
                return false;
            }
        });
        dispatcherChain.add(new Dispatcher(CustomEvent.TAKE_PAUSE.name()) {
            @Override
            protected boolean dispatch(Message message) {
                updateStateIfEnded();
                state.onTakePause(message.from());
                updateStateIfEnded();
                return false;
            }
        });
        dispatcherChain.add(new Dispatcher(Event.DISCONNECTION.name()) {
            @Override
            protected boolean dispatch(Message message) {
                updateStateIfEnded();
                state.onDisconnection(message.from());
                updateStateIfEnded();
                return false;
            }
        });
        dispatcherChain.add(new Dispatcher(CustomEvent.START_GAME.name()) {
            @Override
            protected boolean dispatch(Message message) {
                updateStateIfEnded();
                state.onStartGame(message.from());
                updateStateIfEnded();
                return false;
            }
        });
    }
}
