package com.elfiady.event.infrastructure.listener.command;

import com.elfiady.event.core.model.event.Event;

import java.io.Serializable;
import java.util.Map;

public interface IMapCommand extends Serializable {

	Map<Long, Map<Long , Event>> execute(Map<Long,Map<Long , Event>>calendarContext);

	Long getIdCalendar();

}
