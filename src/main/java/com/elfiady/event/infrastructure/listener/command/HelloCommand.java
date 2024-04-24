package com.elfiady.event.infrastructure.listener.command;

import com.elfiady.event.core.model.event.Event;

import java.util.Map;

public class HelloCommand implements IMapCommand {

	@Override
	public Map<Long, Map<Long, Event>> execute(final Map<Long, Map<Long, Event>> calendarContext) {
		// TODO Auto-generated method stub
		return null;
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
		return "HelloCommand [getIdCalendar()=" + getIdCalendar() + ", getClass()=" + getClass() + ", hashCode()="
				+ hashCode() + ", toString()=" + super.toString() + "]";
	}

}
