package dev.congx.project;

import javax.persistence.Entity;
import dev.congx.utils.RandomStringGenerator;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

import java.util.regex.Pattern;

@Entity
public class Project extends PanacheEntity {
  public String app;
  private String namespace;

  public String create(String app) {
    Pattern nsRegex = Pattern.compile("[a-z0-9]([-a-z0-9]*[a-z0-9])?");

    this.app = app;

    String r;
    do {
      r = RandomStringGenerator.generateRandomName().toLowerCase();
    } while (!nsRegex.matcher(r).matches());

    this.namespace = r;
    return this.namespace;
  }

  public String getNamespace() {
    return this.namespace;
  }
}
