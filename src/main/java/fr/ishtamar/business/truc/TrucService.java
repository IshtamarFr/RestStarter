package fr.ishtamar.business.truc;


import fr.ishtamar.starter.exceptionhandler.EntityNotFoundException;

public interface TrucService {
    Truc getTrucById(final Long id) throws EntityNotFoundException;
}
