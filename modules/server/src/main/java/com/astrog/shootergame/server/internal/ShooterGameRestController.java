package com.astrog.shootergame.server.internal;

import com.astrog.shootergame.common.messaging.CustomEvent;
import com.astrog.shootergame.common.messaging.Event;
import com.astrog.shootergame.server.domain.Dispatcher;
import com.astrog.shootergame.server.domain.RestController;
import com.astrog.shootergame.server.domain.ServerState;
import com.astrog.shootergame.server.domain.model.Message;
import com.astrog.shootergame.server.internal.database.Score;
import com.astrog.shootergame.server.internal.database.ScoreRepository;

import java.util.ArrayList;
import java.util.List;

import static com.astrog.shootergame.common.messaging.CustomEvent.RESPONSE_LEADERS_TABLE;
import static com.astrog.shootergame.common.messaging.MessageFormatter.formatMessage;
import static com.astrog.shootergame.common.messaging.serialization.ObjectToStringSerializer.serialize;


public class ShooterGameRestController extends RestController {

    public static ShooterGameRestController currentController;
    private final List<Dispatcher> dispatcherChain = new ArrayList<>();
    private final ScoreRepository scoreRepository = new ScoreRepository();
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
        dispatcherChain.add(new Dispatcher(CustomEvent.REQUEST_LOGIN.name()) {
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
        dispatcherChain.add(new Dispatcher(CustomEvent.START_GAME.name()) {
            @Override
            protected boolean dispatch(Message message) {
                updateStateIfEnded();
                state.onStartGame(message.from());
                updateStateIfEnded();
                return false;
            }
        });
        dispatcherChain.add(new Dispatcher(CustomEvent.GET_LEADER_TABLE.name()) {
            @Override
            protected boolean dispatch(Message message) {
                List<Score> scores = scoreRepository.getScores();
                List<String> formattedScores = scores.stream()
                    .map(score -> score.getName() + ": " + score.getWinsCount()).toList();
                System.out.println("Send hibernate message");
                message.from().print(formatMessage(RESPONSE_LEADERS_TABLE.name(), serialize(formattedScores)));
                return false;
            }
        });
    }
}
