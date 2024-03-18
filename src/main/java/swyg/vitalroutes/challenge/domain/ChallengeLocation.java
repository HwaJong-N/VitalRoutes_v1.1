package swyg.vitalroutes.challenge.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChallengeLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long locationId;
    private int sequence;
    private String fileName;
    private double latitude;
    private double longitude;

    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    public static ChallengeLocation createLocation(int seq, String fileName, double[] info) {
        ChallengeLocation challengeLocation = new ChallengeLocation();
        challengeLocation.setSequence(seq);
        challengeLocation.setFileName(fileName);
        challengeLocation.setLatitude(info[0]);
        challengeLocation.setLongitude(info[1]);
        return challengeLocation;
    }
}
