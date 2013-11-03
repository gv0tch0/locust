package io.github.gv0tch0.locust.webapi;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.annotation.XmlElement;

/**
 * Translates {@code IllegalArgumentException}s into {@code Response}s.
 * @author Nik Kolev
 */
@Provider
public class ValidationViolationMapper implements ExceptionMapper<IllegalArgumentException> {
    @Override
    public Response toResponse(IllegalArgumentException iae) {
        return Response.status(400).entity(new ValidationViolationEntity(iae.getMessage())).build();
    }

    /** Because JSON marshalling requires thousand little classes (with default ctors too). */
    private static class ValidationViolationEntity {
        @XmlElement(name="error")
        private String _error;

        private ValidationViolationEntity() {
        }
        
        private ValidationViolationEntity(String error) {
            _error = error;
        }
    }
}
