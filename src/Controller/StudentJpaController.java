/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import schoolmanagement.domain.Education;
import schoolmanagement.domain.Student;
import schoolmanagement.domain.exceptions.NonexistentEntityException;

/**
 *
 * @author User1
 */
public class StudentJpaController implements Serializable {

    public StudentJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Student student) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Education education = student.getEducation();
            if (education != null) {
                education = em.getReference(education.getClass(), education.getId());
                student.setEducation(education);
            }
            em.persist(student);
            if (education != null) {
                education.getStudents().add(student);
                education = em.merge(education);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Student student) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Student persistentStudent = em.find(Student.class, student.getId());
            Education educationOld = persistentStudent.getEducation();
            Education educationNew = student.getEducation();
            if (educationNew != null) {
                educationNew = em.getReference(educationNew.getClass(), educationNew.getId());
                student.setEducation(educationNew);
            }
            student = em.merge(student);
            if (educationOld != null && !educationOld.equals(educationNew)) {
                educationOld.getStudents().remove(student);
                educationOld = em.merge(educationOld);
            }
            if (educationNew != null && !educationNew.equals(educationOld)) {
                educationNew.getStudents().add(student);
                educationNew = em.merge(educationNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = student.getId();
                if (findStudent(id) == null) {
                    throw new NonexistentEntityException("The student with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Student student;
            try {
                student = em.getReference(Student.class, id);
                student.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The student with id " + id + " no longer exists.", enfe);
            }
            Education education = student.getEducation();
            if (education != null) {
                education.getStudents().remove(student);
                education = em.merge(education);
            }
            em.remove(student);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Student> findStudentEntities() {
        return findStudentEntities(true, -1, -1);
    }

    public List<Student> findStudentEntities(int maxResults, int firstResult) {
        return findStudentEntities(false, maxResults, firstResult);
    }

    private List<Student> findStudentEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select object(o) from Student as o");
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Student findStudent(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Student.class, id);
        } finally {
            em.close();
        }
    }

    public int getStudentCount() {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select count(o) from Student as o");
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
