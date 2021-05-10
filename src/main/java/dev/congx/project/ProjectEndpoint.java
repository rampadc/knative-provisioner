package dev.congx.project;

import org.jboss.logging.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/project")
public class ProjectEndpoint {

  private static final Logger LOG = Logger.getLogger(ProjectEndpoint.class);

  @GET
  @Path("/new")
  @QueryParam("app")
  public String create(@QueryParam("app") String app) {
    if (app == null) {
      LOG.error("User did not include ?app=X");
      throw new WebApplicationException("User did not include query param 'app': ?app=X", Response.Status.BAD_REQUEST);
    }
    if (app.isEmpty()) {
      LOG.error("App name was not include, user input ?app=");
      throw new WebApplicationException("App name was not include, user input ?app=", Response.Status.BAD_REQUEST);
    }

    Project p = new Project();
    p.create(app);
    LOG.info("Project created with namespace " + p.getNamespace());
    p.persist();
    LOG.info("Project information persisted to database");
    return p.getNamespace();
  }
}
