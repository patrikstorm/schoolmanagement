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
import schoolmanagement.domain.Education;

public class EducationDAO {

    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("PU");

    static Scanner sc = new Scanner(System.in);

    public static void createEducation() {
        System.out.println("What is the name of the new education?");
        String name = sc.nextLine();

        EntityManager em = emf.createEntityManager();

        Education education = new Education(name);

        em.getTransaction().begin();

        em.persist(education);

        em.getTransaction().commit();
    }

    public static void updateEducation(Long id) {

        EntityManager em = emf.createEntityManager();

        Education education = em.find(Education.class, id);

        System.out.println("What is the new name for the education?");
        String name = sc.nextLine();

        em.getTransaction().begin();
        education.setName(name);
        em.getTransaction().commit();

    }

    public static void deleteEducation(Long id) {

        EntityManager em = emf.createEntityManager();

        Education education = em.find(Education.class, id);

        em.getTransaction().begin();

        em.remove(education);

        em.getTransaction().commit();

    }

    public static void showEducation(Long id) {

        EntityManager em = emf.createEntityManager();

        Education education = em.find(Education.class, id);

        System.out.println(education);

    }

    public static void addCourse(Long courseId, Long educationId) {

        EntityManager em = emf.createEntityManager();

        Education education = em.find(Education.class, educationId);

        Course course = em.find(Course.class, courseId);

        em.getTransaction().begin();

        education.addCourse(course);

        em.persist(education);

        em.getTransaction().commit();

    }

    public static void showEducationCourses() {

        System.out.println("Courses:");

        EducationDAO.showAll().forEach(education -> {
            System.out.println("Course: [" + education.getId() + "] - " + education.getName());
            education.getCourses().forEach(System.out::println);
        });;
    }

    public static List<Education> showAll() {

        EntityManager em = emf.createEntityManager();
        List<Education> educations = em.createQuery("SELECT t FROM Education t", Education.class).getResultList();
        for (Education education : educations) {
            System.out.println(education);
        }
        return educations;
    }

}
