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
import static acmecollege.utility.MyConstants.PEER_TUTOR_SUBRESOURCE_NAME;
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
import acmecollege.ejb.ACMECollegeService;
import acmecollege.entity.PeerTutor;

@Path(PEER_TUTOR_SUBRESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PeerTutorResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMECollegeService service;

    @Inject
    protected SecurityContext sc;

    @GET
    @RolesAllowed({ADMIN_ROLE})
    public Response getPeerTutors() {
        LOG.debug("retrieving all peer tutors ...");
        List<PeerTutor> peerTutors = service.getAll(PeerTutor.class, PeerTutor.FIND_ALL);
        Response response = Response.ok(peerTutors).build();
        return response;
    }

    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getPeerTutorById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("try to retrieve specific peer tutor " + id);
        Response response = null;
        PeerTutor peerTutor = service.getById(PeerTutor.class, PeerTutor.QUERY_PEER_TUTOR_BY_ID, id);;
        
  
       response = Response.status(peerTutor == null ? Status.NOT_FOUND : Status.OK).entity(peerTutor).build();
        return response;
    }

    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addPeerTutor(PeerTutor newPeerTutor) {
        Response response = null;
        PeerTutor newPeerTutorWithIdTimestamps = service.persistEntity(PeerTutor.class, newPeerTutor);
        response = Response.ok(newPeerTutorWithIdTimestamps).build();
        return response;
    }

    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deletePeerTutorById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int peerTutorId) {
    	LOG.debug("deleting a peer tutor...");
    	Response response = null;
        service.deletePeerTutorById(peerTutorId);
        response = Response.ok("Peer Tutor with ID" + peerTutorId + "has been deleted.").build();
        LOG.debug("Deleting peerTutor by Id = {}", peerTutorId);
        return response;
 
    }
}