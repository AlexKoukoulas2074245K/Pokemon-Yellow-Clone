package util;

import java.util.ArrayList;

import events.ScriptedEvent;

public class EventQueue {
	
	private ArrayList<ScriptedEvent> elements;

	public EventQueue()
	{
		elements = new ArrayList<ScriptedEvent>();
	}
	
	public void add(ScriptedEvent se)
	{
		elements.add(se);
	}
	
	public ScriptedEvent peek()
	{
		return elements.get(0);
	}
	
	public ScriptedEvent last()
	{
		return elements.get(size() - 1);
	}
	
	public int size()
	{
		return elements.size();
	}
	
	public void pop()
	{
		elements.remove(0);
	}
	
	public void remove(int i)
	{
		elements.remove(i);
	}
	
	public void remove(ScriptedEvent se)
	{
		if(elements.contains(se))
			elements.remove(se);
	}
}	
