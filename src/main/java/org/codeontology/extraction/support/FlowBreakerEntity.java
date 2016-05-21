package org.codeontology.extraction.support;

import org.codeontology.extraction.Entity;

public interface FlowBreakerEntity<E> extends Entity<E> {

    void tagTargetedLabel();

    String getTargetedLabel();
}
