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
            if (transaction != null) {
                transaction.rollback();
            }
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
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public boolean existeNif(String nif) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Contamos cuántos clientes tienen ese nif
            Long count = session.createQuery("select count(c) from Cliente c where c.fiscalNumber = :nif", Long.class)
                    .setParameter("nif", nif)
                    .uniqueResult();
            return count != null && count > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Ante la duda, dejamos pasar (o lanzamos error)
        }
    }

    // Recoge el siguiente Nº de Cliente desde la BBDD. Si por alguna razón (improbable) se borran todos los clientes, no se resetea y sigue en el último que dejó.
    public int obtenerSiguienteId() {
        try (org.hibernate.Session session = com.mycompany.util.HibernateUtil.getSessionFactory().openSession()) {

            String nombreBBDD = "factusoft_db";

            String sql = "SELECT AUTO_INCREMENT FROM information_schema.TABLES "
                    + "WHERE TABLE_NAME = 'clientes' AND TABLE_SCHEMA = :bbdd";

            // Usamos consulta nativa pasando el parámetro y recuperando Object (más seguro)
            Object resultado = session.createNativeQuery(sql, Object.class)
                    .setParameter("bbdd", nombreBBDD)
                    .uniqueResult();

            if (resultado != null) {
                // MySQL devuelve esto como BigInteger o Long, lo convertimos a int
                return ((Number) resultado).intValue();
            }

            System.out.println("ALERTA: No se encontró el ID. Verifica que la BBDD se llama '" + nombreBBDD + "'");
            return 1;

        } catch (Exception e) {
            System.err.println("Error al obtener ID: " + e.getMessage());
            e.printStackTrace(); // Mira la consola si sigue saliendo 1
            return 1;
        }
    }

    public Cliente obtenerPorId(int id) {
        try (org.hibernate.Session session = com.mycompany.util.HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Cliente.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
