package fr.ishtamar.starter.util;

import java.util.List;

public interface EntityMapper<D,E> {
    D toDto(E entity);
    List<D> toDto(List<E> entityList);
}
