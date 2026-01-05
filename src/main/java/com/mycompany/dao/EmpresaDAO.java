package com.mycompany.dao;

import com.mycompany.dominio.Empresa;
import com.mycompany.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class EmpresaDAO {

    public void guardarConfiguracion(Empresa empresa) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(empresa);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public Empresa getEmpresa() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Obtenemos la primera empresa que encontremos (normalmente solo hay 1)
            return session.createQuery("from Empresa", Empresa.class)
                    .setMaxResults(1)
                    .uniqueResult();
        }
    }
}
