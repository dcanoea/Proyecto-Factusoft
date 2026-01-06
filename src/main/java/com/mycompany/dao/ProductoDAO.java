package com.mycompany.dao;

import com.mycompany.dominio.Producto;
import com.mycompany.util.HibernateUtil;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ProductoDAO {

    public void guardar(Producto producto) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(producto);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Listar todos
    public List<Producto> listarTodos() {
        try (org.hibernate.Session session = com.mycompany.util.HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Producto", Producto.class).list();
        }
    }

    // Buscar (por código o descripción)
    public List<Producto> buscarPorTermino(String termino) {
        try (org.hibernate.Session session = com.mycompany.util.HibernateUtil.getSessionFactory().openSession()) {
            String hql = "from Producto p where p.code like :term or p.description like :term";
            return session.createQuery(hql, Producto.class)
                    .setParameter("term", "%" + termino + "%")
                    .list();
        }
    }

    // Eliminar
    public void eliminar(int id) {
        org.hibernate.Transaction transaction = null;
        try (org.hibernate.Session session = com.mycompany.util.HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Producto producto = session.get(Producto.class, id);
            if (producto != null) {
                session.remove(producto);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Obtener por ID (Lo necesitaremos para Editar luego)
    public Producto obtenerPorId(int id) {
        try (org.hibernate.Session session = com.mycompany.util.HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Producto.class, id);
        }
    }

    // Obtener Siguiente ID (Igual que en Clientes)
    public int obtenerSiguienteId() {
        try (org.hibernate.Session session = com.mycompany.util.HibernateUtil.getSessionFactory().openSession()) {
            String nombreBBDD = "factusoft_db"; // Asegúrate que es tu bbdd
            String sql = "SELECT AUTO_INCREMENT FROM information_schema.TABLES "
                    + "WHERE TABLE_NAME = 'productos' AND TABLE_SCHEMA = :bbdd";

            Object resultado = session.createNativeQuery(sql, Object.class)
                    .setParameter("bbdd", nombreBBDD)
                    .uniqueResult();

            if (resultado != null) {
                return ((Number) resultado).intValue();
            }
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    // Buscar Producto exacto por CÓDIGO (Para editar/borrar)
    public Producto buscarPorCodigo(String codigo) {
        try (org.hibernate.Session session = com.mycompany.util.HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Producto p where p.code = :codigo", Producto.class)
                    .setParameter("codigo", codigo)
                    .uniqueResult();
        } catch (Exception e) {
            return null;
        }
    }
}
