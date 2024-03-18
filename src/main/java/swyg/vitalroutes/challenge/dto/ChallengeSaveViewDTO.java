package swyg.vitalroutes.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class ChallengeSaveViewDTO {
    private List<Map<String, String>> types;
    private List<Map<String, String>> tags;
}
