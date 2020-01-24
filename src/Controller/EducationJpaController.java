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
import schoolmanagement.domain.Student;
import schoolmanagement.domain.exceptions.NonexistentEntityException;

/**
 *
 * @author User1
 */
public class EducationJpaController implements Serializable {

    public EducationJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Education education) {
        if (education.getStudents() == null) {
            education.setStudents(new ArrayList<Student>());
        }
        if (education.getCourses() == null) {
            education.setCourses(new ArrayList<Course>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Student> attachedStudents = new ArrayList<Student>();
            for (Student studentsStudentToAttach : education.getStudents()) {
                studentsStudentToAttach = em.getReference(studentsStudentToAttach.getClass(), studentsStudentToAttach.getId());
                attachedStudents.add(studentsStudentToAttach);
            }
            education.setStudents(attachedStudents);
            List<Course> attachedCourses = new ArrayList<Course>();
            for (Course coursesCourseToAttach : education.getCourses()) {
                coursesCourseToAttach = em.getReference(coursesCourseToAttach.getClass(), coursesCourseToAttach.getId());
                attachedCourses.add(coursesCourseToAttach);
            }
            education.setCourses(attachedCourses);
            em.persist(education);
            for (Student studentsStudent : education.getStudents()) {
                Education oldEducationOfStudentsStudent = studentsStudent.getEducation();
                studentsStudent.setEducation(education);
                studentsStudent = em.merge(studentsStudent);
                if (oldEducationOfStudentsStudent != null) {
                    oldEducationOfStudentsStudent.getStudents().remove(studentsStudent);
                    oldEducationOfStudentsStudent = em.merge(oldEducationOfStudentsStudent);
                }
            }
            for (Course coursesCourse : education.getCourses()) {
                coursesCourse.getEducations().add(education);
                coursesCourse = em.merge(coursesCourse);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Education education) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Education persistentEducation = em.find(Education.class, education.getId());
            List<Student> studentsOld = persistentEducation.getStudents();
            List<Student> studentsNew = education.getStudents();
            List<Course> coursesOld = persistentEducation.getCourses();
            List<Course> coursesNew = education.getCourses();
            List<Student> attachedStudentsNew = new ArrayList<Student>();
            for (Student studentsNewStudentToAttach : studentsNew) {
                studentsNewStudentToAttach = em.getReference(studentsNewStudentToAttach.getClass(), studentsNewStudentToAttach.getId());
                attachedStudentsNew.add(studentsNewStudentToAttach);
            }
            studentsNew = attachedStudentsNew;
            education.setStudents(studentsNew);
            List<Course> attachedCoursesNew = new ArrayList<Course>();
            for (Course coursesNewCourseToAttach : coursesNew) {
                coursesNewCourseToAttach = em.getReference(coursesNewCourseToAttach.getClass(), coursesNewCourseToAttach.getId());
                attachedCoursesNew.add(coursesNewCourseToAttach);
            }
            coursesNew = attachedCoursesNew;
            education.setCourses(coursesNew);
            education = em.merge(education);
            for (Student studentsOldStudent : studentsOld) {
                if (!studentsNew.contains(studentsOldStudent)) {
                    studentsOldStudent.setEducation(null);
                    studentsOldStudent = em.merge(studentsOldStudent);
                }
            }
            for (Student studentsNewStudent : studentsNew) {
                if (!studentsOld.contains(studentsNewStudent)) {
                    Education oldEducationOfStudentsNewStudent = studentsNewStudent.getEducation();
                    studentsNewStudent.setEducation(education);
                    studentsNewStudent = em.merge(studentsNewStudent);
                    if (oldEducationOfStudentsNewStudent != null && !oldEducationOfStudentsNewStudent.equals(education)) {
                        oldEducationOfStudentsNewStudent.getStudents().remove(studentsNewStudent);
                        oldEducationOfStudentsNewStudent = em.merge(oldEducationOfStudentsNewStudent);
                    }
                }
            }
            for (Course coursesOldCourse : coursesOld) {
                if (!coursesNew.contains(coursesOldCourse)) {
                    coursesOldCourse.getEducations().remove(education);
                    coursesOldCourse = em.merge(coursesOldCourse);
                }
            }
            for (Course coursesNewCourse : coursesNew) {
                if (!coursesOld.contains(coursesNewCourse)) {
                    coursesNewCourse.getEducations().add(education);
                    coursesNewCourse = em.merge(coursesNewCourse);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = education.getId();
                if (findEducation(id) == null) {
                    throw new NonexistentEntityException("The education with id " + id + " no longer exists.");
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
            Education education;
            try {
                education = em.getReference(Education.class, id);
                education.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The education with id " + id + " no longer exists.", enfe);
            }
            List<Student> students = education.getStudents();
            for (Student studentsStudent : students) {
                studentsStudent.setEducation(null);
                studentsStudent = em.merge(studentsStudent);
            }
            List<Course> courses = education.getCourses();
            for (Course coursesCourse : courses) {
                coursesCourse.getEducations().remove(education);
                coursesCourse = em.merge(coursesCourse);
            }
            em.remove(education);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Education> findEducationEntities() {
        return findEducationEntities(true, -1, -1);
    }

    public List<Education> findEducationEntities(int maxResults, int firstResult) {
        return findEducationEntities(false, maxResults, firstResult);
    }

    private List<Education> findEducationEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select object(o) from Education as o");
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Education findEducation(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Education.class, id);
        } finally {
            em.close();
        }
    }

    public int getEducationCount() {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select count(o) from Education as o");
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
