package swyg.vitalroutes.challenge.dto;

import lombok.Data;
import swyg.vitalroutes.challenge.domain.ChallengeLocation;

@Data
public class LocationResponseDTO {
    private int sequence;
    private String imageURL;
    private double latitude;
    private double longitude;

    public LocationResponseDTO(ChallengeLocation challengeLocation) {
        sequence = challengeLocation.getSequence();
        imageURL = challengeLocation.getFileName();
        latitude = challengeLocation.getLatitude();
        longitude = challengeLocation.getLongitude();
    }
}
