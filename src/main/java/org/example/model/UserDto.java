package org.example.model;

public class UserDto {
    Language language;
    Step step;
    Holiday holiday;

    public UserDto(Language language, Step step, Holiday holiday) {
        this.language = language;
        this.step = step;
        this.holiday = holiday;
    }

    public UserDto() {
    }


    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
    }

    public Holiday getHoliday() {
        return holiday;
    }

    public void setHoliday(Holiday holiday) {
        this.holiday = holiday;
    }
}
