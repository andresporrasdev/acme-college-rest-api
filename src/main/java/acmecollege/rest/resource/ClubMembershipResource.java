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
//import static acmecollege.utility.MyConstants.STUDENT_COURSE_PEER_TUTOR_RESOURCE_PATH;
import static acmecollege.utility.MyConstants.CLUB_MEMBERSHIP_RESOURCE_NAME;
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
import javax.ws.rs.PUT;
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
import acmecollege.entity.ClubMembership;
//import acmecollege.entity.PeerTutor;
import acmecollege.entity.SecurityUser;
import acmecollege.entity.Student;
//import acmecollege.entity.StudentClub;

@Path(CLUB_MEMBERSHIP_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ClubMembershipResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMECollegeService service;

    @Inject
    protected SecurityContext sc;

    @GET
    @RolesAllowed({ADMIN_ROLE}) 
    public Response getClubMemberships() {
        LOG.debug("retrieving all club memberships...");
        List<ClubMembership> clubMemberships = service.getAll(ClubMembership.class, ClubMembership.FIND_ALL);
        LOG.debug("Club memberships found = {}", clubMemberships);
        Response response = Response.ok(clubMemberships).build();
        return response;
    }

    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getClubMembershipById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("try to retrieve club membership with ID: {}", id);
        Response response = null;
        ClubMembership clubMembership = service.getClubMembershipById(id);

        if (clubMembership == null) {
        	response = Response.status(Status.NOT_FOUND).build();
        } if (sc.isCallerInRole(ADMIN_ROLE)) {
            response = Response.status(Status.OK).entity(clubMembership).build();
        } else if (sc.isCallerInRole(USER_ROLE)) {
            WrappingCallerPrincipal wCallerPrincipal = (WrappingCallerPrincipal) sc.getCallerPrincipal();
            SecurityUser sUser = (SecurityUser) wCallerPrincipal.getWrapped();
            Student student = sUser.getStudent();
            if (student != null && clubMembership != null && student.equals(clubMembership.getCard().getOwner())) {
                response = Response.status(Status.OK).entity(clubMembership).build();
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
    public Response addClubMembership(ClubMembership newClubMembership) {
        LOG.debug("creating a club membership...");
        Response response = null;
        ClubMembership newClubMembershipWithIdTimestamps = service.persistClubMembership(newClubMembership);
        response = Response.ok(newClubMembershipWithIdTimestamps).build();
        return response;
    }

    @PUT
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response updateClubMembership(@PathParam(RESOURCE_PATH_ID_ELEMENT) int clubmembershipId, ClubMembership clubMembershipWithUpdates) {
        LOG.debug("updating a club membership...");
    	Response response = null;
    	ClubMembership updatedClubMembership = service.updateClubMembership(clubmembershipId, clubMembershipWithUpdates);
        LOG.debug("Updating club membership with Id = {}", clubmembershipId);
        response = Response.ok(updatedClubMembership).build();
        return response;
    }
    
    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deleteClubMembership(@PathParam(RESOURCE_PATH_ID_ELEMENT) int clubmembershipId) {
        LOG.debug("deleting a club membership...");
    	Response response = null;
    	service.deleteClubMembershipById(clubmembershipId);
        response = Response.ok("Club membership with ID" + clubmembershipId + "has been deleted.").build();
        LOG.debug("Deleting club membership by Id = {}", clubmembershipId);
        return response;
    }
}