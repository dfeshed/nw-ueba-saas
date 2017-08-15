package presidio.webapp.dto;

import java.io.Serializable;

/**
 * Swagger is not supporting generics, so for supporing swagger YAML + Code generation but implement no generics DTO
 */
public class AlertSingleEntityResponseBean extends SingleEntityResponseBean<Alert> implements Serializable {


}
