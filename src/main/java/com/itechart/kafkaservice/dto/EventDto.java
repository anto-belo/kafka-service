package com.itechart.kafkaservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {

    private String correlation;

    /**
     * Type of this event. e.g. {@code client.create}.
     *
     * <p>This attribute is required.
     */
//    @NotEmpty
    private String type;

    /**
     * Group of this event. This can be used to handle specific cases where we
     * can provide more information about the event.
     */
    private String group;

    /**
     * Name of the source that generated this event. This can be name of the
     * application that is generating this event. e.g. "sf-accounts".
     *
     * <p>This attribute is required.
     */
//    @NotEmpty
    private String source;

    /**
     * Location of the source that generated this event. This can be an IP address
     * of a machine if the host IP is known or some other identifier.
     */
    private String location;

    /**
     * Timestamp of creation of this event.
     *
     * <p>This attribute is required.
     */
    @NotNull
    private LocalDateTime created;

    /**
     * Context attributes are used to have information related to the context of
     * this event. e.g. It can contain id of the client that being created.
     *
     * <p>The values going into this map should be serializable/deserializable into
     * JSON by Jackson.
     */
    private Map<String, Object> context;

    /**
     * The payload to be sent as part of this event. On the producing end, this can
     * be any object. On the consuming end, this would be an instance of
     * {@code Map<String,Object>} type.
     *
     * <p>The object should be serializable/deserializable into JSON by Jackson. If
     * you want to send binary data in the event, consider encoding the binary data
     * into BASE64.
     *
     * <p>This attribute is required.
     */
    private Object payload;

    public EventDto(String source, @NotNull LocalDateTime created) {
        this.source = source;
        this.created = created;
    }
}
