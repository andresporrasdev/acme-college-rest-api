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
import static acmecollege.utility.MyConstants.PEER_TUTOR_REGISTRATION_RESOURCE_NAME;
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
import acmecollege.entity.PeerTutorRegistration;
import acmecollege.entity.PeerTutorRegistrationPK;
import acmecollege.entity.SecurityUser;
import acmecollege.entity.Student;


@Path(PEER_TUTOR_REGISTRATION_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PeerTutorRegistrationResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMECollegeService service;

    @Inject
    protected SecurityContext sc;

    @GET
    @RolesAllowed({ADMIN_ROLE})
    public Response getPeerTutorRegistrations() {
        LOG.debug("retrieving all Peer Tutor Registrations...");
        List<PeerTutorRegistration> peerTutorRegistrations = service.getAll(PeerTutorRegistration.class, PeerTutorRegistration.FIND_ALL);
        LOG.debug("Peer Tutor Registrations found = {}", peerTutorRegistrations);
        Response response = Response.ok(peerTutorRegistrations).build();
        return response;
    }
    
    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path("/{studentId}/{courseId}")
    public Response getPeerTutorRegistrationById(@PathParam("studentId") int studentId, @PathParam("courseId") int courseId) {
        LOG.debug("Attempting to retrieve peer tutor registration with studentId: {} and courseId: {}", studentId, courseId);
        Response response = null;
        
        PeerTutorRegistrationPK id = new PeerTutorRegistrationPK(studentId, courseId);
        PeerTutorRegistration peerTutorRegistration = service.getPeerTutorRegistrationById(id);
        
        if (peerTutorRegistration == null) {
            return Response.status(Status.NOT_FOUND).build();
        } if (sc.isCallerInRole(ADMIN_ROLE)) {
            response = Response.status(Status.OK).entity(peerTutorRegistration).build();
        } 
        else if (sc.isCallerInRole(USER_ROLE)) {
            WrappingCallerPrincipal wCallerPrincipal = (WrappingCallerPrincipal) sc.getCallerPrincipal();
            SecurityUser sUser = (SecurityUser) wCallerPrincipal.getWrapped();
            Student student = sUser.getStudent();

            if (student != null && student.equals(peerTutorRegistration.getStudent())) {
                response = Response.status(Status.OK).entity(peerTutorRegistration).build();
            } else {
                throw new ForbiddenException("User attempting to access a resource they do not own (wrong userId)");
            }
        } else {
            response = Response.status(Status.BAD_REQUEST).build();
        }
        
        return response;
    }


    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addPeerTutorRegistration(PeerTutorRegistration newPeerTutorRegistration) {
        LOG.debug("New Peer Tutor Registration...");
        Response response = null;
        PeerTutorRegistration newPeerTutorRegistrationWithIdTimestamps = service.persistEntity(PeerTutorRegistration.class, newPeerTutorRegistration);
        response = Response.ok(newPeerTutorRegistrationWithIdTimestamps).build();
        return response;
    }


    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deletePeerTutorRegistrationById(@PathParam("studentId") int studentId, @PathParam("courseId") int courseId) {
        LOG.debug("Deleting Peer Tutor Registration...");
    	Response response = null;
    	PeerTutorRegistrationPK id = new PeerTutorRegistrationPK(studentId, courseId);
    	service.deletePeerTutorRegistrationById(id);
        response = Response.ok(new PeerTutorRegistrationPK(studentId, courseId)).build();
        LOG.debug("Deleting Peer Tutor Registration with Course ID: {}, Student ID: {}", courseId, studentId);

        return response;
    }
}