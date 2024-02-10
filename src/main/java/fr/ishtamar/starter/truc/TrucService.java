package fr.ishtamar.starter.truc;


import fr.ishtamar.starter.exceptionhandler.EntityNotFoundException;

public interface TrucService {
    Truc getTrucById(final Long id) throws EntityNotFoundException;
}
