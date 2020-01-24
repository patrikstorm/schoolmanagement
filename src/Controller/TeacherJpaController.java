/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import schoolmanagement.domain.Course;
import schoolmanagement.domain.Teacher;
import schoolmanagement.domain.exceptions.NonexistentEntityException;

/**
 *
 * @author User1
 */
public class TeacherJpaController implements Serializable {

    public TeacherJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Teacher teacher) {
        if (teacher.getCourses() == null) {
            teacher.setCourses(new ArrayList<Course>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Course> attachedCourses = new ArrayList<Course>();
            for (Course coursesCourseToAttach : teacher.getCourses()) {
                coursesCourseToAttach = em.getReference(coursesCourseToAttach.getClass(), coursesCourseToAttach.getId());
                attachedCourses.add(coursesCourseToAttach);
            }
            teacher.setCourses(attachedCourses);
            em.persist(teacher);
            for (Course coursesCourse : teacher.getCourses()) {
                coursesCourse.getTeachers().add(teacher);
                coursesCourse = em.merge(coursesCourse);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Teacher teacher) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Teacher persistentTeacher = em.find(Teacher.class, teacher.getId());
            List<Course> coursesOld = persistentTeacher.getCourses();
            List<Course> coursesNew = teacher.getCourses();
            List<Course> attachedCoursesNew = new ArrayList<Course>();
            for (Course coursesNewCourseToAttach : coursesNew) {
                coursesNewCourseToAttach = em.getReference(coursesNewCourseToAttach.getClass(), coursesNewCourseToAttach.getId());
                attachedCoursesNew.add(coursesNewCourseToAttach);
            }
            coursesNew = attachedCoursesNew;
            teacher.setCourses(coursesNew);
            teacher = em.merge(teacher);
            for (Course coursesOldCourse : coursesOld) {
                if (!coursesNew.contains(coursesOldCourse)) {
                    coursesOldCourse.getTeachers().remove(teacher);
                    coursesOldCourse = em.merge(coursesOldCourse);
                }
            }
            for (Course coursesNewCourse : coursesNew) {
                if (!coursesOld.contains(coursesNewCourse)) {
                    coursesNewCourse.getTeachers().add(teacher);
                    coursesNewCourse = em.merge(coursesNewCourse);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = teacher.getId();
                if (findTeacher(id) == null) {
                    throw new NonexistentEntityException("The teacher with id " + id + " no longer exists.");
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
            Teacher teacher;
            try {
                teacher = em.getReference(Teacher.class, id);
                teacher.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The teacher with id " + id + " no longer exists.", enfe);
            }
            List<Course> courses = teacher.getCourses();
            for (Course coursesCourse : courses) {
                coursesCourse.getTeachers().remove(teacher);
                coursesCourse = em.merge(coursesCourse);
            }
            em.remove(teacher);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Teacher> findTeacherEntities() {
        return findTeacherEntities(true, -1, -1);
    }

    public List<Teacher> findTeacherEntities(int maxResults, int firstResult) {
        return findTeacherEntities(false, maxResults, firstResult);
    }

    private List<Teacher> findTeacherEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select object(o) from Teacher as o");
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Teacher findTeacher(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Teacher.class, id);
        } finally {
            em.close();
        }
    }

    public int getTeacherCount() {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select count(o) from Teacher as o");
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
