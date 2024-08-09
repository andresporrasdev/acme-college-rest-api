/********************************************************************************************************2*4*w*
 * File:  StudentResource.java Course materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
 * 
 * Updated by:  Group 04
 *   041096703, Alessandra, Prunzel Kittlaus
 *   041066068, Alex, Hulford
 *   041056717, Andres Camilo, Porras Becerra
 *   041004332, Sewuese, Ayu
 * 
 */
package acmecollege.rest.resource;

import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.COURSE_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static acmecollege.utility.MyConstants.USER_ROLE;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.soteria.WrappingCallerPrincipal;

import acmecollege.ejb.ACMECollegeService;
import acmecollege.entity.Course;
import acmecollege.entity.PeerTutorRegistration;
import acmecollege.entity.SecurityUser;
import acmecollege.entity.Student;

@Path(COURSE_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CourseResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMECollegeService service;

    @Inject
    protected SecurityContext sc;

    @GET
    @RolesAllowed({ADMIN_ROLE})
    public Response getCourses() {
        LOG.debug("retrieving all courses...");
        List<Course> courses = service.getAll(Course.class, Course.ALL_COURSES_QUERY);
        LOG.debug("Courses found = {}", courses);
        Response response = Response.ok(courses).build();
        return response;	
    }

 
    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getCourseById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("try to retrieve a course with ID: {}", id);
        Response response = null;
        Course course = service.getById(Course.class, Course.FIND_COURSE_BY_ID, id);

        if (course == null) {
        	response = Response.status(Status.NOT_FOUND).build();
        } if (sc.isCallerInRole(ADMIN_ROLE)) {
            response = Response.status(Status.OK).entity(course).build();
        } else if (sc.isCallerInRole(USER_ROLE)) {
            WrappingCallerPrincipal wCallerPrincipal = (WrappingCallerPrincipal) sc.getCallerPrincipal();
            SecurityUser sUser = (SecurityUser) wCallerPrincipal.getWrapped();
            Student student = sUser.getStudent();
            if (student != null && isStudentEnrolledInCourse(student, course)) {
                response = Response.status(Status.OK).entity(course).build();
            } else {
                throw new ForbiddenException("User trying to access resource it does not own (wrong userid)");
            }
        } else {
            response = Response.status(Status.BAD_REQUEST).build();
        }
        return response;
    }
   

    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addCourse(Course newCourse) {
        LOG.debug("creating a course...");
        Response response = null;
        Course newCourseWithIdTimestamps = service.persistEntity(Course.class, newCourse);
        response = Response.ok(newCourseWithIdTimestamps).build();
        return response;
    }

    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deleteCourse(@PathParam(RESOURCE_PATH_ID_ELEMENT) int courseId) {
        LOG.debug("deleting a course...");
    	Response response = null;
    	service.deleteCourseById(courseId);
        response = Response.ok("Course with ID" + courseId + "has been deleted.").build();
        LOG.debug("Deleting course by Id = {}", courseId);
        return response;
    }
    
    private boolean isStudentEnrolledInCourse(Student student, Course course) {
        for (PeerTutorRegistration registration : course.getPeerTutorRegistrations()) {
            if (registration.getStudent().equals(student)) {
                return true;
            }
        }
        return false;
    }
}