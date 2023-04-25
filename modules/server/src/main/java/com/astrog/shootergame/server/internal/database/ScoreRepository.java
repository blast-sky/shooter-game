package com.astrog.shootergame.server.internal.database;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class ScoreRepository {

    private final SessionFactory sessionFactory = SessionFactoryConfiguration.getFactory();

    private Session openSession() {
        return sessionFactory.openSession();
    }

    public List<Score> getScores() {
        try (Session session = openSession()) {
            Query<Score> query = session.createQuery("FROM Score ORDER BY winsCount DESC", Score.class);
            return query.getResultList();
        }
    }

    public void increaseScoreToPlayerOrCreateAndIncrease(String player) {
        try(Session session = openSession()) {
            Transaction transaction = session.beginTransaction();

            Query<Score> query = session.createQuery("FROM Score s WHERE name = :name", Score.class);
            query.setParameter("name", player);
            Score score = query.uniqueResult();

            if(score == null) {
                score = new Score(null, player, 1L);
            } else {
                score.setWinsCount(score.getWinsCount() + 1);
            }

            session.persist(score);

            transaction.commit();
        }
    }
}
