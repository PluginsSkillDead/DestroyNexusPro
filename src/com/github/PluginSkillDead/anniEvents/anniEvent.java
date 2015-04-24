package com.github.PluginSkillDead.anniEvents;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.event.Listener;

public abstract class AnniEvent
{
  private static Map<String, List<Wrapper>> eventHandlers = new HashMap();

  public static void registerListener(Listener listener)
  {
    for (Method m : listener.getClass().getMethods())
    {
      AnnihilationEvent e = (AnnihilationEvent)m.getAnnotation(AnnihilationEvent.class);
      if (e != null)
      {
        Class[] params = m.getParameterTypes();
        if ((params.length == 1) && (AnniEvent.class.isAssignableFrom(params[0])))
        {
          String key = params[0].getSimpleName();
          List wrappers = (List)eventHandlers.get(key);
          if (wrappers == null)
            wrappers = new ArrayList();
          wrappers.add(new Wrapper(listener, m));
          eventHandlers.put(key, wrappers);
        }
      }
    }
  }

  public static void callEvent(AnniEvent event)
  {
    List wrappers = (List)eventHandlers.get(event.getClass().getSimpleName());
    if (wrappers != null)
    {
      for (Wrapper wrap : wrappers)
      {
        wrap.call(event);
      }
    }
  }

  public int hashCode()
  {
    int prime = 31;
    int result = 1;
    result = 31 * result + getClass().getSimpleName().hashCode();
    return result;
  }

  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!obj.getClass().isAssignableFrom(getClass()))
      return false;
    if (obj.getClass().getSimpleName().equals(getClass().getSimpleName()))
      return true;
    return false;
  }

  private static class Wrapper
  {
    private final Listener listener;
    private final Method m;

    public Wrapper(Listener listener, Method m) {
      this.listener = listener;
      this.m = m;
    }

    public void call(AnniEvent e)
    {
      try
      {
        if (!this.m.isAccessible())
          this.m.setAccessible(true);
        this.m.invoke(this.listener, new Object[] { e });
      }
      catch (IllegalAccessException|IllegalArgumentException|InvocationTargetException e1)
      {
        e1.printStackTrace();
      }
    }
  }
}