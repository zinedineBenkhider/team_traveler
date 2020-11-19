package com.example.teamtraveler.presentation.activities.recyclerView;

public class ParticipantViewModel {
    private String particpantName;
    private String participantOpinion;

    public ParticipantViewModel( String particpantName, String participantOpinion) {
        this.particpantName = particpantName;
        this.participantOpinion = participantOpinion;
    }

    public String getParticpantName() {
        return particpantName;
    }

    public void setParticpantName(String particpantName) {
        this.particpantName = particpantName;
    }

    public String getParticipantOpinion() {
        return participantOpinion;
    }

    public void setParticipantOpinion(String participantOpinion) {
        this.participantOpinion = participantOpinion;
    }
}
