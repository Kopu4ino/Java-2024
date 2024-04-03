package edu.java.service.updater;

import edu.java.domain.model.Link;
import edu.java.domain.model.Update;
import java.util.Optional;

public interface LinkUpdater {
    String getSupportDomain();

    void setLastUpdateTime(Link link);

    Optional<Update> fetchUpdate(Link link);
}
