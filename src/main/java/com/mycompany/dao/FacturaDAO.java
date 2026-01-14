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
            // join fetch carga cliente. 
            // Añadido: "ORDER BY f.id DESC" para ver las últimas primero
            return session.createQuery("from Factura f join fetch f.cliente order by f.id desc", Factura.class).list();
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

    /**
     * Calcula el siguiente número para una serie dada (ej: "F" o "R"). Devuelve
     * formato: "F-0001", "R-0005", etc.
     */
    public String getSiguienteNumeroFactura(String serie) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // 1. Buscamos el número más alto SOLO dentro de esa serie (F o R)
            String hql = "SELECT MAX(f.number) FROM Factura f WHERE f.series = :serieParam";

            Integer maxNumero = (Integer) session.createQuery(hql)
                    .setParameter("serieParam", serie)
                    .uniqueResult();

            // 2. Si no hay facturas de esa serie, empezamos por el 1
            int siguienteNumero = (maxNumero == null) ? 1 : maxNumero + 1;
            
            

            // 3. Formateamos el resultado (Serie + Guion + 4 dígitos)
            // Ejemplo: "F-0001"
            return serie + "-" + String.format("%04d", siguienteNumero);

        } catch (Exception e) {
            e.printStackTrace();
            return serie + "-ERROR";
        }
    }

    // Método para recuperar una factura específica por su serie y número
    public com.mycompany.dominio.Factura obtenerPorSerieYNumero(String serie, int numero) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Factura f WHERE f.series = :serie AND f.number = :num";
            return session.createQuery(hql, com.mycompany.dominio.Factura.class)
                    .setParameter("serie", serie)
                    .setParameter("num", numero)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
