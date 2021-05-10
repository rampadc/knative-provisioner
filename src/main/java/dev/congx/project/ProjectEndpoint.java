package dev.congx.project;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/project")
public class ProjectEndpoint {

  @Inject
  KubernetesClient kubernetesClient;

  private static final Logger LOG = Logger.getLogger(ProjectEndpoint.class);

  @GET
  @Path("/new/{app}")
  @Transactional
  public String create(@PathParam("app") String app) {
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

    // Create a namespace
    LOG.info("Creating namespace...");
    Namespace ns = new NamespaceBuilder().withNewMetadata()
      .withName(p.getNamespace())
      .addToLabels("app", app)
      .endMetadata().build();
    this.kubernetesClient.namespaces().create(ns);
    LOG.info("Namespace created: " + p.getNamespace());

    // Apply resource quotas
    LOG.info("Applying resource quotas to namespace " + p.getNamespace());
    ResourceQuota quota = new ResourceQuotaBuilder().withNewMetadata()
      .withName("pod-medium")
      .endMetadata().withNewSpec()
      .addToHard("limits.cpu", new Quantity("100m"))
      .addToHard("limits.memory", new Quantity("256Mi"))
      .endSpec().build();
    this.kubernetesClient.resourceQuotas().inNamespace(p.getNamespace()).create(quota);
    LOG.info("Resource quotas applied!");

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
