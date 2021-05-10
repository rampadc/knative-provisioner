package dev.congx.project;

import javax.inject.Inject;
import javax.persistence.Entity;
import javax.persistence.Transient;

import dev.congx.utils.RandomStringGenerator;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Project extends PanacheEntity {
  @Inject
  @Transient
  private KubernetesClient kubernetesClient;

  public String app;
  private String namespace;

  public String create(String app) {
    this.app = app;
    this.namespace = RandomStringGenerator.generateRandomName();

    // Create a namespace
    Namespace ns = new NamespaceBuilder().withNewMetadata()
      .withName(this.namespace)
      .addToLabels("app", this.app)
      .endMetadata().build();
    this.kubernetesClient.namespaces().create(ns);

    // Apply resource quotas
    ResourceQuota quota = new ResourceQuotaBuilder().withNewMetadata()
      .withName("pod-quota")
      .endMetadata().withNewSpec()
      .addToHard("limits.cpu", new Quantity("500mi"))
      .addToHard("limits.memory", new Quantity("256Mi"))
      .endSpec().build();
    this.kubernetesClient.resourceQuotas().inNamespace(this.namespace).create(quota);

    return this.namespace;
  }

  public String getNamespace() {
    return this.namespace;
  }
}
