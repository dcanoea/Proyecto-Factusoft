package com.mycompany.dao;

import com.mycompany.dominio.Factura;
import com.mycompany.util.HibernateUtil;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class FacturaDAO {

    public void guardarFactura(Factura factura) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // persist: ideal para guardar nuevas entidades
            // Como Factura tiene cascade=ALL en 'lineas', guardará también las líneas
            session.persist(factura);

            transaction.commit();
            System.out.println("Factura guardada con ID: " + factura.getId());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error al guardar factura: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Factura> listarTodas() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // join fetch carga el cliente asociado de una vez para optimizar rendimiento
            return session.createQuery("from Factura f join fetch f.cliente", Factura.class).list();
        }
    }

    public Factura obtenerPorId(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // left join fetch carga las líneas para tenerlas listas
            String hql = "from Factura f left join fetch f.lineas where f.id = :id";
            return session.createQuery(hql, Factura.class)
                    .setParameter("id", id)
                    .uniqueResult();
        }
    }
}
