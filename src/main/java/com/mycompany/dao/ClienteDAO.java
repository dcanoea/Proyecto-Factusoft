package com.mycompany.dao;

import com.mycompany.dominio.Cliente;
import com.mycompany.util.HibernateUtil;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class ClienteDAO {

    public void guardar(Cliente cliente) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            // merge: guarda si es nuevo, actualiza si ya existe (por ID)
            session.merge(cliente); 
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.err.println("Error guardando cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Cliente> listarTodos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // HQL: "from Cliente" se refiere a la CLASE, no a la tabla
            return session.createQuery("from Cliente", Cliente.class).list();
        }
    }
    
    public List<Cliente> buscarPorNombre(String nombre) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "from Cliente where fiscalName like :nombre OR fiscalNumber like :nombre";
            Query<Cliente> query = session.createQuery(hql, Cliente.class);
            query.setParameter("nombre", "%" + nombre + "%");
            return query.list();
        }
    }
    
    public void eliminar(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Cliente cliente = session.get(Cliente.class, id);
            if (cliente != null) {
                session.remove(cliente);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
}