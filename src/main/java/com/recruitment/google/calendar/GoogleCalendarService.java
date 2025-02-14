package com.recruitment.google.calendar;

import com.recruitment.interview.InterviewSlot;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import java.time.ZoneId;
import java.util.TimeZone;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleCalendarService {

    private final Calendar googleCalendarService;

    public String createCalendarEvent(String calendarId, InterviewSlot slot) throws Exception {
        Event event = new Event();
        event.setSummary("Interview: " + slot.getCategory() + " - " + slot.getSeniority());

        EventDateTime start = new EventDateTime();
        start.setDateTime(new com.google.api.client.util.DateTime(
                slot.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
        start.setTimeZone(TimeZone.getDefault().getID());
        event.setStart(start);

        EventDateTime end = new EventDateTime();
        end.setDateTime(new com.google.api.client.util.DateTime(
                slot.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
        end.setTimeZone(TimeZone.getDefault().getID());
        event.setEnd(end);

        Event created = googleCalendarService.events().insert(calendarId, event).execute();
        return created.getId();
    }

    public void updateCalendarEvent(String calendarId, InterviewSlot slot) throws Exception {
        if (slot.getGoogleCalendarEventId() == null) {
            return;
        }
        Event event = googleCalendarService.events().get(calendarId, slot.getGoogleCalendarEventId()).execute();
        event.setSummary("Interview Updated: " + slot.getCategory() + " - " + slot.getSeniority());

        EventDateTime start = new EventDateTime();
        start.setDateTime(new com.google.api.client.util.DateTime(
                slot.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
        event.setStart(start);

        EventDateTime end = new EventDateTime();
        end.setDateTime(new com.google.api.client.util.DateTime(
                slot.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
        event.setEnd(end);

        googleCalendarService.events().update(calendarId, slot.getGoogleCalendarEventId(), event).execute();
    }

    public void deleteCalendarEvent(String calendarId, String eventId) throws Exception {
        if (eventId == null) {
            return;
        }
        googleCalendarService.events().delete(calendarId, eventId).execute();
    }
}