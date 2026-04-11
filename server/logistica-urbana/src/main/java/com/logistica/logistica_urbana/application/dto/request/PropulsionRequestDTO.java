package com.logistica.logistica_urbana.application.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * DTO abstracto base para la deserialización polimórfica del campo
 * {@code propulsion}
 * en los requests de creación de vehículos.
 *
 * <p>
 * Jackson usa el valor del campo {@code tipoPropulsion} del request padre
 * como discriminador para instanciar la subclase concreta correcta.
 * </p>
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 * @see PropulsionTermicaRequestDTO
 * @see PropulsionElectricaRequestDTO
 * @see PropulsionHibridaRequestDTO
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "tipoPropulsion")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PropulsionTermicaRequestDTO.class, name = "TERMICA"),
        @JsonSubTypes.Type(value = PropulsionElectricaRequestDTO.class, name = "ELECTRICA"),
        @JsonSubTypes.Type(value = PropulsionHibridaRequestDTO.class, name = "HIBRIDA")
})
public abstract class PropulsionRequestDTO {
}
