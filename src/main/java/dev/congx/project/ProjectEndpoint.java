package dev.congx.project;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/project")
public class ProjectEndpoint {

  @Inject
  KubernetesClient kubernetesClient;

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

  @GET
  @Path("/{namespace}/pods")
  public String getPods(@PathParam("namespace") String namespace) {
    PodList podList = this.kubernetesClient.pods().inNamespace(namespace).list();
    StringBuilder list = new StringBuilder();

    for (Pod pod : podList.getItems()) {
      list.append(pod.getMetadata().getName()).append(", ");
    }
    return list.toString();
  }
}
