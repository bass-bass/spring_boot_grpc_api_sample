package com.example.grpc.api.common.util;

import java.util.HashMap;
import java.util.Map;

public class Destructor {

  public interface Destructable {

    public void destroy() throws Exception;
  }

  private static Map<String, Destructable> map = new HashMap<>();

  public static void put(String name, Destructable obj) {
    map.put(name, obj);
  }

  public static Destructable get(String name) {
    return map.get(name);
  }

  public static void cancel(String name) {
    if (map.containsKey(name))
      map.put(name, null);
  }

  public static void destroyAll() throws Exception {
    if (map == null)
      return;
    Map<String, Destructable> _map = map;
    map = null;
    if (_map.size() == 0)
      return;

    for (Destructable d : _map.values()) {
      if (d == null)
        continue;
      d.destroy();
    }
  }
}
