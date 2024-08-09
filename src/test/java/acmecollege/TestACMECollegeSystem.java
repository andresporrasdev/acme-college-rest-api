/********************************************************************************************************2*4*w*
 * File:  TestACMECollegeSystem.java
 * Course materials CST 8277
 * Teddy Yap
 * (Original Author) Mike Norman
 *
 *
 * (Modified) @author Student Name
 */
package acmecollege;

import acmecollege.entity.PeerTutor;
import static acmecollege.utility.MyConstants.*;
//import static acmecollege.utility.MyConstants.APPLICATION_API_VERSION;
//import static acmecollege.utility.MyConstants.APPLICATION_CONTEXT_ROOT;
//import static acmecollege.utility.MyConstants.DEFAULT_ADMIN_USER;
//import static acmecollege.utility.MyConstants.DEFAULT_ADMIN_USER_PASSWORD;
//import static acmecollege.utility.MyConstants.DEFAULT_USER;
//import static acmecollege.utility.MyConstants.DEFAULT_USER_PASSWORD;
//import static acmecollege.utility.MyConstants.STUDENT_RESOURCE_NAME;

import acmecollege.entity.Student;
import acmecollege.entity.*;

//New imports
import static org.junit.jupiter.api.Assertions.*;

import javax.ws.rs.core.MediaType;
import java.lang.invoke.MethodHandles;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import org.junit.jupiter.api.Order;

@SuppressWarnings("unused")

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestACMECollegeSystem {
    private static final Class<?> _thisClaz = MethodHandles.lookup().lookupClass();
    private static final Logger logger = LogManager.getLogger(_thisClaz);

    static final String HTTP_SCHEMA = "http";
    static final String HOST = "localhost";
    static final int PORT = 8080;
    static Student student;
    static PeerTutor peertutor;

    // Test fixture(s)
    static URI uri;
    static HttpAuthenticationFeature adminAuth;
    static HttpAuthenticationFeature userAuth;

    @BeforeAll
    public static void oneTimeSetUp() throws Exception {
        logger.debug("oneTimeSetUp");
        uri = UriBuilder
            .fromUri(APPLICATION_CONTEXT_ROOT + APPLICATION_API_VERSION)
            .scheme(HTTP_SCHEMA)
            .host(HOST)
            .port(PORT)
            .build();
        adminAuth = HttpAuthenticationFeature.basic(DEFAULT_ADMIN_USER, DEFAULT_ADMIN_USER_PASSWORD);
        userAuth = HttpAuthenticationFeature.basic(DEFAULT_USER, DEFAULT_USER_PASSWORD);
        student = new Student();
        student.setFirstName("Andres");
        student.setLastName("Porras");
        peertutor = new PeerTutor();
        peertutor.setPeerTutor("Sewuese", "Hulford", "SoftwareProg");
    }

    protected WebTarget webTarget;
    @BeforeEach
    public void setUp() {
        Client client = ClientBuilder.newClient(
            new ClientConfig().register(MyObjectMapperProvider.class).register(new LoggingFeature()));
        webTarget = client.target(uri);
        


    }

    @Test
    @Order(1)
    public void test01_all_students_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            //.register(userAuth)
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<Student> students = response.readEntity(new GenericType<List<Student>>(){});
        assertThat(students, is(not(empty())));
        assertThat(students, hasSize(1));
    }
    
    //Test Student
    @Test
    @Order(2)
    /**
     *  To get all Students with admin
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    public void test02_get_all_students_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request()
            .get();
        List<Student> students = response.readEntity(new GenericType<List<Student>>(){});
        assertThat(response.getStatus(), is(200));
        assertThat(students, is(not(empty())));
    }

		
    @Test
    @Order(3)
    /**
     * To get all Students with user role
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    public void test03_get_all_students_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(403));
        }


    @Test
    @Order(4)
    /**
     * To get Student with id using admin 
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    public void test04_get_student_by_id_adminrole() throws JsonMappingException, JsonProcessingException {
            Response response = webTarget
                   .register(adminAuth)
                   .path(STUDENT_RESOURCE_NAME+"/1")
                   .request()
                   .get();
            assertThat(response.getStatus(), is(200));
        Student student = response.readEntity(new GenericType<Student>() {	    	 
        });
        assertThat(student.getId(), is(1));
       }


    @Test
    @Order(5)
    /**
     * To get Student with id using user role
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    public void test05_get_student_by_id_userrole() throws JsonMappingException, JsonProcessingException {
            Response response = webTarget
                   .register(userAuth)
                   .path(STUDENT_RESOURCE_NAME+"/1")
                   .request()
                   .get();
            assertThat(response.getStatus(), is(200));
    }

    @Test
    @Order(6)	
    /**
     * To add to Student with admin role
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    public void test06_add_student_adminrole() throws JsonMappingException, JsonProcessingException{
        Student student = new Student();
        student.setFirstName("Andres");
        student.setLastName("Porras");
        Response response = webTarget
       .register(adminAuth)
       .path(STUDENT_RESOURCE_NAME)
       .request()
       .post(Entity.json(student));
            assertThat(response.getStatus(), is(200));
    }


    @Test
    @Order(7)
    /**
     * To add to Student with user role
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    public void test07_add_student_userrole() throws JsonMappingException, JsonProcessingException{
        Student student = new Student();
        student.setFirstName("Andres");
        student.setLastName("Porras");
        Response response = webTarget
        .register(userAuth)
       .path(STUDENT_RESOURCE_NAME)
       .request()
       .post(Entity.json(student));
        assertThat(response.getStatus(), is(403));
}

    @Test
    @Order(8)
    /**
     * To delete from Student with admin role
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    public void test08_delete_student_by_id_adminrole() throws JsonMappingException, JsonProcessingException {

        Response response1 = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request()
            .get();
        List<Student> students = response1.readEntity(new GenericType<List<Student>>(){});

         String id = Integer.toString(students.get(students.size() -1).getId());

        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME+"/"+ id)
            .request()
            .delete();
        assertThat(response.getStatus(), is(200));
    }




    @Test
    @Order(9)
    /**
     * To delete from Student table with user role
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    public void test09_delete_student_by_id_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
                .register(userAuth)        	
                .path(STUDENT_RESOURCE_NAME+"/"+student.getId())
                .request()
                .delete();
        assertThat(response.getStatus(), is(403));
    }


    //Test Student club
    @Test
    @Order(10)
    /**
    * Tests the retrieval of all student clubs with an admin role.
    *
    * @throws JsonMappingException If there's an issue with JSON mapping.
    * @throws JsonProcessingException If there's an issue with JSON processing.
    */
    public void test10_all_student_clubs_adminrole() throws JsonMappingException, JsonProcessingException {
            Response response = webTarget
        .register(adminAuth)
        .path(STUDENT_CLUB_RESOURCE_NAME)
        .request()
        .get();
    assertThat(response.getStatus(), is(200));
    }

    @Test
    @Order(11)
    /**
 * Tests the retrieval of all student clubs with a user role.
 *
 * @throws JsonMappingException If there's an issue with JSON mapping.
 * @throws JsonProcessingException If there's an issue with JSON processing.
 */
    public void test11_all_student_clubs_userrole() throws JsonMappingException, JsonProcessingException {
            Response response = webTarget
        .register(userAuth)
        .path(STUDENT_CLUB_RESOURCE_NAME)
        .request()
        .get();
    assertThat(response.getStatus(), is(200));
    }

    @Test
    @Order(12)
    public void test12_add_student_club_userrole() throws JsonMappingException, JsonProcessingException {
            AcademicStudentClub academicStudentClub = new AcademicStudentClub();
            Response response = webTarget
        .register(userAuth)
        .path(STUDENT_CLUB_RESOURCE_NAME)
        .request()
        .post(Entity.json(academicStudentClub)); 
    assertThat(response.getStatus(), is(403));
    }

    @Test
    @Order(13)
    /**
    * Tests the deletion of a student club by ID with a user role.
    *
    * @throws JsonMappingException If there's an issue with JSON mapping.
    * @throws JsonProcessingException If there's an issue with JSON processing.
    */ 
    public void test13_delete_student_club_by_id_userrole() throws JsonMappingException, JsonProcessingException {
            Response response = webTarget
        .register(userAuth)
        .path(STUDENT_CLUB_RESOURCE_NAME + "/" + 2)
        .request()
        .delete(); 
    assertThat(response.getStatus(), is(403));
    }
    /**
     * Tests adding a club membership with a user role.
     *
     * @throws JsonMappingException If there's an issue with JSON mapping.
     * @throws JsonProcessingException If there's an issue with JSON processing.
     */
    @Test
    @Order(14)
    public void test14_add_club_membership_userrole() throws JsonMappingException, JsonProcessingException {
        ClubMembership clubMembership = new ClubMembership();
        Response response = webTarget
                .register(userAuth)
                .path(CLUB_MEMBERSHIP_RESOURCE_NAME)
                .request()
                .post(Entity.json(clubMembership));
        assertThat(response.getStatus(), is(403));
    }

    /**
     * Tests retrieving all club memberships with an admin role.
     *
     * @throws JsonMappingException If there's an issue with JSON mapping.
     * @throws JsonProcessingException If there's an issue with JSON processing.
     */
    @Test
    @Order(15)
    public void test15_all_club_memberships_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
                .register(adminAuth)
                .path(CLUB_MEMBERSHIP_RESOURCE_NAME)
                .request()
                .get();
        assertThat(response.getStatus(), is(200));
        List<ClubMembership> clubmemberships = response.readEntity(new GenericType<List<ClubMembership>>() {});
        assertThat(clubmemberships, is(not(empty())));
    }

    /**
     * Tests deleting a club membership by ID with an admin role.
     *
     * @throws JsonMappingException If there's an issue with JSON mapping.
     * @throws JsonProcessingException If there's an issue with JSON processing.
     */
    @Test
    @Order(16)
    public void test16_delete_club_membership_by_id_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
                .register(adminAuth)
                .path(CLUB_MEMBERSHIP_RESOURCE_NAME + "/" + 2)
                .request()
                .delete();
        assertThat(response.getStatus(), is(200));
    }

    /**
     * Tests deleting a club membership by ID with a user role.
     *
     * @throws JsonMappingException If there's an issue with JSON mapping.
     * @throws JsonProcessingException If there's an issue with JSON processing.
     */
    @Test
    @Order(17)
    public void test17_delete_club_membership_by_id_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
                .register(userAuth)
                .path(CLUB_MEMBERSHIP_RESOURCE_NAME + "/" + 2)
                .request()
                .delete();
        assertThat(response.getStatus(), is(403));
    }

    //Test Course
    /**
     * Tests retrieving all courses with an admin role.
     *
     * @throws JsonMappingException If there's an issue with JSON mapping.
     */
    @Test
    @Order(18)
    public void test18_getAllCourses_adminrole() throws JsonMappingException {
        Response response = webTarget
                .register(adminAuth)
                .path(COURSE_RESOURCE_NAME)
                .request()
                .get();
        assertEquals(response.getStatus(), 200);
    }

    /**
     * Tests retrieving all courses with a user role (should return 403 Forbidden).
     *
     * @throws JsonMappingException If there's an issue with JSON mapping.
     * @throws JsonProcessingException If there's an issue with JSON processing.
     */
    @Test
    @Order(19)
    public void test19_getAllCourses_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
                .register(userAuth)
                .path(COURSE_RESOURCE_NAME)
                .request()
                .get();
        assertEquals(response.getStatus(), 403);
    }

    /**
     * Tests adding a new course with an admin role.
     *
     * @throws JsonMappingException If there's an issue with JSON mapping.
     */
    @Test
    @Order(20)
    public void test20_postCourse_adminrole() throws JsonMappingException {
        Course course = new Course("cst2389", "CourseTest", 2100, "summer", 2, (byte) 0);
        try (Response response = webTarget
                .register(adminAuth)
                .path("course")
                .request()
                .post(Entity.json(course))) {
            assertEquals(response.getStatus(), 200);
        }
    }

    /**
     * Tests adding a new course with a user role (should return 403 Forbidden).
     *
     * @throws JsonMappingException If there's an issue with JSON mapping.
     */
    @Test
    @Order(21)
    public void test21_postCourse_userrole() throws JsonMappingException {
        Course course = new Course();
        course.setCourseTitle("Java EE");
        try (Response response = webTarget
                .register(userAuth)
                .path("course")
                .request()
                .post(Entity.json(course))) {
            assertEquals(response.getStatus(), 403);
        }
    }

    /**
     * Tests deleting a course by ID with a user role (should return 403 Forbidden).
     *
     * @throws JsonMappingException If there's an issue with JSON mapping.
     * @throws JsonProcessingException If there's an issue with JSON processing.
     */
    @Test
    @Order(22)
    public void test22_deleteCourse_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
                .register(userAuth)
                .path(COURSE_RESOURCE_NAME + "/1")
                .request()
                .delete();
        assertThat(response.getStatus(), is(403));
    }


    @Test
    @Order(23)
    /**
     *  To get all Peer tutor with admin
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    public void test23_get_all_peertutor_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
                            .register(adminAuth)
            .path(PEER_TUTOR_SUBRESOURCE_NAME)
            .request()
            .get();
        List<PeerTutor> peertutors = response.readEntity(new GenericType<List<PeerTutor>>(){});
        assertThat(response.getStatus(), is(200));
        assertThat(peertutors, is(not(empty())));
    }


    @Test
    @Order(24)
    /**
     * To get all Peer tutor  with user role
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    public void test24_get_all_peertutor_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
                 .path(PEER_TUTOR_SUBRESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(403));
        }


         @Test
         @Order(25)
         /**
          * To get Peer tutor  with id using admin 
          * @throws JsonMappingException
          * @throws JsonProcessingException
          */
         public void test25_get_peertutor_by_id_adminrole() throws JsonMappingException, JsonProcessingException {
                 Response response = webTarget
                        .register(adminAuth)
                        .path(PEER_TUTOR_SUBRESOURCE_NAME+"/1")
                        .request()
                        .get();
                 assertThat(response.getStatus(), is(200));
             PeerTutor peertutor = response.readEntity(new GenericType<PeerTutor>() {	    	 
             });
             assertThat(peertutor.getId(), is(1));
            }

         @Test
         @Order(26)	
         /**
          * To add to Peer tutor with admin role
          * @throws JsonMappingException
          * @throws JsonProcessingException
          */
     public void test26_add_peertutor_adminrole() throws JsonMappingException, JsonProcessingException{
                 PeerTutor peertutor = new PeerTutor("Alessandra", "Brasil", "Database", null);
                 Response response = webTarget
            .register(adminAuth)
            .path(PEER_TUTOR_SUBRESOURCE_NAME)
            .request()
            .post(Entity.json(peertutor));
                 assertThat(response.getStatus(), is(200));
         }


         @Test
         @Order(27)
         /**
          * To add to Peer tutor with user role
          * @throws JsonMappingException
          * @throws JsonProcessingException
          */
     public void test27_add_peertutor_userrole() throws JsonMappingException, JsonProcessingException{
                 PeerTutor peertutor = new PeerTutor("Alessandra", "Brasil", "Database2", null);
                 Response response = webTarget
                        .register(userAuth)
            .path(PEER_TUTOR_SUBRESOURCE_NAME)
            .request()
            .post(Entity.json(peertutor));
                 assertThat(response.getStatus(), is(403));
    }

        @Test
         @Order(28)
         /**
          * To get Peer tutor  with id using user role
          * @throws JsonMappingException
          * @throws JsonProcessingException
          */
         public void test28_get_peertutor_by_id_adminrole() throws JsonMappingException, JsonProcessingException {
                 Response response = webTarget
                        .register(adminAuth)
                        .path(PEER_TUTOR_SUBRESOURCE_NAME+"/1")
                        .request()
                        .get();
                 assertThat(response.getStatus(), is(200));
         }

        @Test
         @Order(29)
         /**
          * To get Peer tutor  with id using user role
          * @throws JsonMappingException
          * @throws JsonProcessingException
          */
         public void test29_get_peertutor_by_id_userrole() throws JsonMappingException, JsonProcessingException {
                 Response response = webTarget
                        .register(userAuth)
                        .path(PEER_TUTOR_SUBRESOURCE_NAME+"/1")
                        .request()
                        .get();
                 assertThat(response.getStatus(), is(200));
         }

    @Test
    @Order(30)
    /**
     * To delete from Peer tutor table with user role
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    public void test30_delete_peertutor_by_id_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
                .register(userAuth)        	
                .path(PEER_TUTOR_SUBRESOURCE_NAME+"/"+peertutor.getId())
                .request()
                .delete();
        assertThat(response.getStatus(), is(403));
    }

    //AcmeCollegeSystem


    /**
     * Test to add a membership cards with user privilege, which will get HTTP 403 Forbidden response,
     * insufficient rights to a resource
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(31)
    public void test31_add_membership_card_userrole() throws JsonMappingException, JsonProcessingException{
        Student owner = new Student();
        owner.setId(1);

        ClubMembership membership = new ClubMembership();
        membership.setId(1);
        MembershipCard card = new MembershipCard();
        card.setClubMembership(membership);
        card.setOwner(owner);
        card.setSigned(true);
        Response response = webTarget
                .register(userAuth)
                .path(MEMBERSHIP_CARD_RESOURCE_NAME)
                .request()
                .post(Entity.entity(card, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(403));
    }

    /**
     * Test get all membership cards with admin privilege
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(32)
    public void test32_all_membership_cards_adminrole() throws JsonMappingException, JsonProcessingException{
            Response response = webTarget
                    .register(adminAuth)
                    .path(MEMBERSHIP_CARD_RESOURCE_NAME)
                    .request()
                    .get();
            assertThat(response.getStatus(), is(200));
            logger.debug("WHats the stitch " + response.getStatus());
            List<MembershipCard> cards = response.readEntity(new GenericType<List<MembershipCard>>(){});

            assertThat(cards, is(not(empty())));
    }

    /**
     * Test get all membership cards with user privilege, which will get HTTP 403 Forbidden response,
     * insufficient rights to a resource
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(33)
    public void test33_all_membership_cards_userrole() throws JsonMappingException, JsonProcessingException{
        Response response = webTarget
                .register(userAuth)
                .path(MEMBERSHIP_CARD_RESOURCE_NAME)
                .request()
                .get();
        assertThat(response.getStatus(), is(200));
    }

    @Test
    @Order(34)
    public void test34_membership_card_by_id_adminrole() throws JsonMappingException, JsonProcessingException{
        Response response = webTarget
                .register(adminAuth)
                .path(MEMBERSHIP_CARD_RESOURCE_NAME + "/1")
                .request()
                .get();
        assertThat(response.getStatus(), is(200));
        MembershipCard card = response.readEntity(MembershipCard.class);
        assertThat(card.getId(), is(1));
    }

    /**
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(35)
    public void test35_membership_card_by_id_userrole() throws JsonMappingException, JsonProcessingException{
        Response response = webTarget
                .register(userAuth)
                .path(MEMBERSHIP_CARD_RESOURCE_NAME + "/1")
                .request()
                .get();
        assertThat(response.getStatus(), is(200));

    }

    /**
     * Admin delete a membership card
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(36)
    public void test36_delete_membership_card_by_id_Adminrole() throws JsonMappingException, JsonProcessingException{
        Response response1 = webTarget
                .register(adminAuth)
                .path(MEMBERSHIP_CARD_RESOURCE_NAME)
                .request()
                .get();
        List<MembershipCard> cards = response1.readEntity(new GenericType<List<MembershipCard>>(){});
        String id = Integer.toString(cards.get(cards.size() -1).getId());

        Response response2 = webTarget
                .register(adminAuth)
                .path(MEMBERSHIP_CARD_RESOURCE_NAME + "/" + id)
                .request()
                .delete();
        assertThat(response2.getStatus(), is(200));
    }


    //Peer Tutor Registration

    /**
     * Tests whether a user with the specified role can access all peer tutor registrations.
     * Expected behavior: Response status should be 403 (Forbidden).
     *
     * @throws JsonMappingException If there's an issue with JSON mapping.
     * @throws JsonProcessingException If there's an issue with JSON processing.
     */
    @Test
    @Order(37)
    public void test37_all_peertutor_registrations_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
                .register(userAuth)
                .path(PEER_TUTOR_REGISTRATION_RESOURCE_NAME)
                .request()
                .get();
        assertThat(response.getStatus(), is(403));
    }

    /**
     * Retrieves a specific peer tutor registration by its ID.
     * Expected behavior: Response status should be 200 (OK), and the retrieved registration
     * should have a numeric grade of 100 and a letter grade of "A+".
     *
     * @throws JsonMappingException If there's an issue with JSON mapping.
     * @throws JsonProcessingException If there's an issue with JSON processing.
     */
    @Test
    @Order(38)
    public void test38_get_course_registration_By_Id_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
                .register(userAuth)
                .path("/" + PEER_TUTOR_REGISTRATION_RESOURCE_NAME + "/1/1")
                .request()
                .get();
        assertThat(response.getStatus(), is(200));
        PeerTutorRegistration cr = response.readEntity(new GenericType<PeerTutorRegistration>() {});
        assertThat(cr.getNumericGrade(), is(100));
        assertThat(cr.getLetterGrade(), is("A+"));
    }

    /**
     * Retrieves a specific peer tutor registration by its ID.
     * Similar to the previous test, but with admin role.
     *
     * @throws JsonMappingException If there's an issue with JSON mapping.
     * @throws JsonProcessingException If there's an issue with JSON processing.
     */
    @Test
    @Order(39)
    public void test39_get_course_registration_By_Id_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
                .register(adminAuth)
                .path("/" + PEER_TUTOR_REGISTRATION_RESOURCE_NAME + "/1/1")
                .request()
                .get();
        assertThat(response.getStatus(), is(200));
        PeerTutorRegistration cr = response.readEntity(new GenericType<PeerTutorRegistration>() {});
        assertThat(cr.getNumericGrade(), is(100));
        assertThat(cr.getLetterGrade(), is("A+"));
    }

    /**
     * Tests whether an admin user can access all peer tutor registrations.
     * Expected behavior: Response status should be 200 (OK), and the retrieved list
     * of registrations should not be empty and contain two entries.
     *
     * @throws JsonMappingException If there's an issue with JSON mapping.
     * @throws JsonProcessingException If there's an issue with JSON processing.
     */
    @Test
    @Order(40)
    public void test40_all_peertutor_registrations_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
                .register(adminAuth)
                .path(PEER_TUTOR_REGISTRATION_RESOURCE_NAME)
                .request()
                .get();
        assertThat(response.getStatus(), is(200));
        List<PeerTutorRegistration> cr = response.readEntity(new GenericType<List<PeerTutorRegistration>>() {});
        assertThat(cr, is(not(empty())));
        assertThat(cr, hasSize(2));
    }

        
}