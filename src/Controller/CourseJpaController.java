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
import schoolmanagement.domain.Education;
import schoolmanagement.domain.Teacher;
import schoolmanagement.domain.exceptions.NonexistentEntityException;

/**
 *
 * @author User1
 */
public class CourseJpaController implements Serializable {

    public CourseJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Course course) {
        if (course.getEducations() == null) {
            course.setEducations(new ArrayList<Education>());
        }
        if (course.getTeachers() == null) {
            course.setTeachers(new ArrayList<Teacher>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Education> attachedEducations = new ArrayList<Education>();
            for (Education educationsEducationToAttach : course.getEducations()) {
                educationsEducationToAttach = em.getReference(educationsEducationToAttach.getClass(), educationsEducationToAttach.getId());
                attachedEducations.add(educationsEducationToAttach);
            }
            course.setEducations(attachedEducations);
            List<Teacher> attachedTeachers = new ArrayList<Teacher>();
            for (Teacher teachersTeacherToAttach : course.getTeachers()) {
                teachersTeacherToAttach = em.getReference(teachersTeacherToAttach.getClass(), teachersTeacherToAttach.getId());
                attachedTeachers.add(teachersTeacherToAttach);
            }
            course.setTeachers(attachedTeachers);
            em.persist(course);
            for (Education educationsEducation : course.getEducations()) {
                educationsEducation.getCourses().add(course);
                educationsEducation = em.merge(educationsEducation);
            }
            for (Teacher teachersTeacher : course.getTeachers()) {
                teachersTeacher.getCourses().add(course);
                teachersTeacher = em.merge(teachersTeacher);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Course course) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Course persistentCourse = em.find(Course.class, course.getId());
            List<Education> educationsOld = persistentCourse.getEducations();
            List<Education> educationsNew = course.getEducations();
            List<Teacher> teachersOld = persistentCourse.getTeachers();
            List<Teacher> teachersNew = course.getTeachers();
            List<Education> attachedEducationsNew = new ArrayList<Education>();
            for (Education educationsNewEducationToAttach : educationsNew) {
                educationsNewEducationToAttach = em.getReference(educationsNewEducationToAttach.getClass(), educationsNewEducationToAttach.getId());
                attachedEducationsNew.add(educationsNewEducationToAttach);
            }
            educationsNew = attachedEducationsNew;
            course.setEducations(educationsNew);
            List<Teacher> attachedTeachersNew = new ArrayList<Teacher>();
            for (Teacher teachersNewTeacherToAttach : teachersNew) {
                teachersNewTeacherToAttach = em.getReference(teachersNewTeacherToAttach.getClass(), teachersNewTeacherToAttach.getId());
                attachedTeachersNew.add(teachersNewTeacherToAttach);
            }
            teachersNew = attachedTeachersNew;
            course.setTeachers(teachersNew);
            course = em.merge(course);
            for (Education educationsOldEducation : educationsOld) {
                if (!educationsNew.contains(educationsOldEducation)) {
                    educationsOldEducation.getCourses().remove(course);
                    educationsOldEducation = em.merge(educationsOldEducation);
                }
            }
            for (Education educationsNewEducation : educationsNew) {
                if (!educationsOld.contains(educationsNewEducation)) {
                    educationsNewEducation.getCourses().add(course);
                    educationsNewEducation = em.merge(educationsNewEducation);
                }
            }
            for (Teacher teachersOldTeacher : teachersOld) {
                if (!teachersNew.contains(teachersOldTeacher)) {
                    teachersOldTeacher.getCourses().remove(course);
                    teachersOldTeacher = em.merge(teachersOldTeacher);
                }
            }
            for (Teacher teachersNewTeacher : teachersNew) {
                if (!teachersOld.contains(teachersNewTeacher)) {
                    teachersNewTeacher.getCourses().add(course);
                    teachersNewTeacher = em.merge(teachersNewTeacher);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = course.getId();
                if (findCourse(id) == null) {
                    throw new NonexistentEntityException("The course with id " + id + " no longer exists.");
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
            Course course;
            try {
                course = em.getReference(Course.class, id);
                course.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The course with id " + id + " no longer exists.", enfe);
            }
            List<Education> educations = course.getEducations();
            for (Education educationsEducation : educations) {
                educationsEducation.getCourses().remove(course);
                educationsEducation = em.merge(educationsEducation);
            }
            List<Teacher> teachers = course.getTeachers();
            for (Teacher teachersTeacher : teachers) {
                teachersTeacher.getCourses().remove(course);
                teachersTeacher = em.merge(teachersTeacher);
            }
            em.remove(course);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Course> findCourseEntities() {
        return findCourseEntities(true, -1, -1);
    }

    public List<Course> findCourseEntities(int maxResults, int firstResult) {
        return findCourseEntities(false, maxResults, firstResult);
    }

    private List<Course> findCourseEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select object(o) from Course as o");
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Course findCourse(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Course.class, id);
        } finally {
            em.close();
        }
    }

    public int getCourseCount() {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select count(o) from Course as o");
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
