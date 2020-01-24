/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CRUD;

import java.util.List;
import java.util.Scanner;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import schoolmanagement.domain.Course;
import schoolmanagement.domain.Teacher;

public class TeacherDAO {

    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("PU");

    static Scanner sc = new Scanner(System.in);

    public static void createTeacher() {
        System.out.println("What is the name of the new teacher?");
        String name = sc.nextLine();

        EntityManager em = emf.createEntityManager();

        Teacher teacher = new Teacher(name);

        em.getTransaction().begin();

        em.persist(teacher);

        em.getTransaction().commit();
    }

    public static void updateTeacher(Long id) {

        EntityManager em = emf.createEntityManager();

        Teacher teacher = em.find(Teacher.class, id);

        System.out.println("What is the new name for the teacher?");
        String name = sc.nextLine();

        em.getTransaction().begin();
        teacher.setName(name);
        em.getTransaction().commit();

    }

    public static void deleteTeacher(Long id) {

        EntityManager em = emf.createEntityManager();

        Teacher teacher = em.find(Teacher.class, id);

        em.getTransaction().begin();

        em.remove(teacher);

        em.getTransaction().commit();

    }

    public static void showTeacher(Long id) {

        EntityManager em = emf.createEntityManager();

        Teacher teacher = em.find(Teacher.class, id);

        System.out.println(teacher);

    }

    public static void addCourse(Long courseId, Long teacherId) {
        EntityManager em = emf.createEntityManager();

        Teacher teacher = em.find(Teacher.class, teacherId);

        Course course = em.find(Course.class, courseId);

        em.getTransaction().begin();

        teacher.addCourse(course);

        em.persist(teacher);

        em.getTransaction().commit();
    }

//    public static void showTeacherCourses() {
//
//        System.out.println("Teachers:");
//
//        TeacherDAO.showAll().forEach(teacher -> {
//            System.out.println("Teacher: [" + teacher.getId() + "] - " + teacher.getName());
//            teacher.getCourses().forEach(System.out::println);
//        });;
//    }

    public static List<Teacher> showAll() {

        EntityManager em = emf.createEntityManager();
        List<Teacher> teachers = em.createQuery("SELECT t FROM Teacher t", Teacher.class).getResultList();
        for (Teacher teacher : teachers) {
            System.out.println(teacher);
        }
        return teachers;
    }

}
