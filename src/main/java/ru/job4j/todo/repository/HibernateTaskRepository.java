package ru.job4j.todo.repository;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import ru.job4j.todo.model.Task;

import java.util.Collection;
import java.util.Optional;

import lombok.AllArgsConstructor;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class HibernateTaskRepository implements TaskRepository {

    private final SessionFactory sf;

    @Override
    public Task save(Task task) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = sf.openSession();
            transaction = session.beginTransaction();
            session.save(task);
            transaction.commit();
            return task;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public boolean deleteById(int id) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = sf.openSession();
            transaction = session.beginTransaction();
            Query query = session.createQuery(
                    "DELETE Task WHERE id = :id");
            query.setParameter("id", id);
            int updatedRows = query.executeUpdate();
            transaction.commit();

            return updatedRows > 0;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public boolean update(Task task) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = sf.openSession();
            transaction = session.beginTransaction();
            Query query = session.createQuery(
                    "UPDATE Task t SET t.title = :title, t.description = :description WHERE t.id = :id"
            );
            query.setParameter("title", task.getTitle());
            query.setParameter("description", task.getDescription());
            query.setParameter("id", task.getId());

            int updatedRows = query.executeUpdate();
            transaction.commit();
            return updatedRows > 0;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }


    @Override
    public Collection<Task> findAll() {
        Transaction transaction = null;
        Session session = null;
        try {
            session = sf.openSession();
            transaction = session.beginTransaction();
            Query query = session.createQuery("from Task");
            transaction.commit();
            return query.list();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public Collection<Task> findCompleted() {
       return findTasks(true);
    }

    @Override
    public Collection<Task> findNew() {
        return findTasks(false);
    }

    public Collection<Task> findTasks(boolean bool) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = sf.openSession();
            transaction = session.beginTransaction();
            Query<Task> query = session.createQuery(
                    "from Task as t where t.done = :done", Task.class);
            query.setParameter("done", bool);
            var result = query.list();
            transaction.commit();
            return result;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public Optional<Task> findById(int id) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = sf.openSession();
            transaction = session.beginTransaction();

            Query<Task> query = session.createQuery(
                    "from Task as t where t.id = :id", Task.class);
            query.setParameter("id", id);
            Optional<Task> result = query.uniqueResultOptional();

            transaction.commit();
            return result;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public boolean doneTask(Task task) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = sf.openSession();
            transaction = session.beginTransaction();
            Query query = session.createQuery(
                    "UPDATE Task t SET t.done = :done WHERE t.id = :id"
            );
            query.setParameter("done", true);
            query.setParameter("id", task.getId());

            int updatedRows = query.executeUpdate();
            transaction.commit();
            return updatedRows > 0;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
