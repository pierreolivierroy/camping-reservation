package upgrade.challenge.availability.domain;

import lombok.Getter;

@Getter
public enum EventType {

    CAMPSITE_RESERVED("CampsiteReservedEvent");

    private String value;

    EventType(String value) {
        this.value = value;
    }
}
