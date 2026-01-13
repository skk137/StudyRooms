package gr.hua.dit.StudyRooms.core.security;

import gr.hua.dit.StudyRooms.core.model.PersonType;

/**
 * Lightweight view of the current authenticated user.
 */
public record CurrentUser(long id, String username, PersonType type) {}

