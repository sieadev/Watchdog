package dev.siea.database.models;

/**
 * The ReportType enum represents the various types of reports that can be made against a user.
 */
public enum ReportType {
    /**
     * Report type for cheating in video games.
     */
    CHEATING_IN_VIDEO_GAME,

    /**
     * Report type for doxxing, which involves publicizing private information.
     */
    DOXXING,

    /**
     * Report type for scamming or fraudulent activities.
     */
    SCAMMING,

    /**
     * Report type for sharing malicious media, including links, texts, or visual material.
     */
    MALICIOUS_MEDIA,

    /**
     * Report type for engaging in hate speech.
     */
    HATE_SPEECH,

    /**
     * Report type for bullying or harassment.
     */
    BULLYING,

    /**
     * Report type for making threats of violence.
     */
    THREATS_OF_VIOLENCE,

    /**
     * Report type for engaging in illegal activities.
     */
    ILLEGAL_ACTIVITY
}