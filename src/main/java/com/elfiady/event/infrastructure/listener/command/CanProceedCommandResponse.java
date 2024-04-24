package com.elfiady.event.infrastructure.listener.command;

import com.elfiady.event.core.model.event.Event;

import java.util.Map;

public class CanProceedCommandResponse implements IMapCommand {

	Map<Long, Map<Long, Event>> localCalendarContext;

	public CanProceedCommandResponse(final Map<Long, Map<Long, Event>> localCalendarContext) {
		this.localCalendarContext = localCalendarContext;
	}

	@Override
	public Map<Long, Map<Long, Event>> execute(final Map<Long, Map<Long, Event>> calendarContext) {
		return localCalendarContext;
	}

	@Override
	public Long getIdCalendar() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "HelloCommandResponse [localCalendarContext=" + localCalendarContext + "]";
	}

}
