package com.example.grpc.api.common.logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Logger {

  static class Configuration extends HashMap<String, Object> {
    public Configuration() {
    }

    public void parse(InputStream is) throws IOException {
      Properties prop = new Properties();
      prop.load(is);
      Set<String> keys = prop.stringPropertyNames();
      for (String key : keys) {
        conf.put(key, prop.get(key));
      }
    }

    public String getString(String name) {
      return getString(name, null);
    }

    public String getString(String name, String def) {
      Object val = get(name);
      return (val instanceof String) ? (String) val : def;
    }
  }

  public static class Keys {
    public static final String SERVER_NAME = "server.name";
    public static final String APP_ID = "app.id";
    public static final String LOGGER_FILE_LEVEL = "logger.file.level";
    public static final String LOGGER_FILE_DIR = "logger.file.dir";
  }

  public static final int LOG_LEVEL_TRACE = 1;
  public static final int LOG_LEVEL_DEBUG = 2;
  public static final int LOG_LEVEL_INFO = 3;
  public static final int LOG_LEVEL_WARN = 4;
  public static final int LOG_LEVEL_ERROR = 5;
  public static final int LOG_LEVEL_FATAL = 6;
  public static final int LOG_LEVEL_ALL = (LOG_LEVEL_TRACE - 1);
  public static final int LOG_LEVEL_OFF = (LOG_LEVEL_FATAL + 1);

  public static volatile boolean isInitialized = false;
  public static final String FORMAT_DATE = "yyyy-MM-dd HH:mm:ss";
  private static final SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT_DATE);

  private final String name;
  private final boolean sysout;

  private static volatile boolean initialized = false;
  private static volatile boolean isDebugAtInit = false;

  private Logger(String name, boolean sysout) {
    this.name = name;
    this.sysout = sysout;
  }

  public String getName() {
    return name;
  }

  private static final ConcurrentMap<String, Logger> loggers = new ConcurrentHashMap<>();
  public static final Configuration conf = new Configuration();

  private static void init() {
    init(null, true);
  }

  public static void init(String path, boolean isDebug) {
    if (!initialized) {
      initialized = true;
      isDebugAtInit = isDebug;

      URL url = null;
      try {
        // こうじゃないと実行環境によって取得できない.
        url = findURLOfDefaultConfigurationFile();
        assert url != null;
        makeProperties(url.openStream(), url.toString());
        if (isDebug)
          System.out.println("Logger conf path => ${path}/logger.properties");
      } catch (Exception e) {
        System.out.println("url->" + (url == null ? null : url.toString()));
        e.printStackTrace(System.out);
        if (isDebug)
          System.out.println("Logger conf path => -");
      }
    }
  }

  private static void makeProperties(InputStream is, String path) {
    try {
      conf.parse(is);
      if (isDebugAtInit)
        System.out.println("Logger init. data load from \"" + path + "\"");
    } catch (Exception e) {
      if (isDebugAtInit)
        System.out.println("[ERROR]" + e.getMessage());
    }
  }

  private static ClassLoader[] getClassLoaders(final Class<?> clazz, boolean isDebug) {
    ClassLoader[] loaders = new ClassLoader[3];
    loaders[0] = clazz.getClassLoader();
    loaders[1] = ClassLoader.getSystemClassLoader();
    loaders[2] = Thread.currentThread().getContextClassLoader();
    return loaders;
  }

  private static URL getResource(ClassLoader classLoader, String resource) {
    try {
      return classLoader.getResource(resource);
    } catch (Exception e) {
      return null;
    }
  }

  private static URL findURLOfDefaultConfigurationFile() {
    ClassLoader[] loaders = getClassLoaders(Logger.class, isDebugAtInit);
    for (ClassLoader loader : loaders) {
      if (loader != null) {
        if (isDebugAtInit) {
          System.out.println("conf_class_loader=>" + loader);
          URL url = loader.getResource("");
          System.out.println("def_path=>" + ((url == null) ? null : url.toString()));
        }

        URL url = getResource(loader, "product/logger.properties");
        if (url != null) {
          if (isDebugAtInit)
            System.out.println("conf_find");
          return url;
        }
      }
    }
    return null;
  }

  public static Logger getLogger(String name) {
    return getLogger(name, false);
  }

  public static Logger getLogger(String name, boolean sysout) {
    if (!initialized)
      init();
    String[] splits = name.split("\\.");
    name = splits[splits.length - 1];
    Logger logger = loggers.get(name);
    if (logger == null) {
      logger = new Logger(name, sysout);
      loggers.putIfAbsent(name, logger);
    }
    return logger;
  }

  public static synchronized Logger getLogger() {
    return getLogger(null);
  }

  protected void log(int level, Object message, Throwable t) {
    boolean writesFile = (level >= getFileLevel());
    if ((!writesFile) || (message == null && t == null))
      return;

    if (message instanceof Throwable && t == null) {
      t = (Throwable) message;
      message = null;
    }

    String datetime = null;
    synchronized (dateFormat) {
      datetime = dateFormat.format(new Timestamp(System.currentTimeMillis()));
    }
    String appId = conf.getString(Keys.APP_ID);
    String serverName = conf.getString(Keys.SERVER_NAME);
    String levelName = getLevelName(level);
    String shortMessage;
    String detailMessage = null;
    if (t != null)
      detailMessage = t.toString();
    if (message != null) {
      shortMessage = message.toString();
    } else {
      // java.lang.RuntimeException : Timed out waiting for operation -> RuntimeException
      String[] splits = detailMessage.split(": \n")[0].split("\\.");
      shortMessage = splits[splits.length - 1];
    }

    // 2011-04-20 21:32:45 [ERROR] NullPointerException
    StringBuilder buf =
            new StringBuilder()
                    .append(datetime)
                    .append(" [")
                    .append(levelName)
                    .append("] ")
                    .append(shortMessage)
                    .append("\n");

    if (detailMessage != null)
      buf.append(detailMessage).append("\n");
    if (t != null) {
      appendStackTrace(buf, t);
      buf.append("-------------------------------------------\n");
    }
    String text = buf.toString();

    // write log file
    String date = datetime.split(" ")[0];
    StringBuilder fileName = new StringBuilder(100).append(getFileName(level));
    if (this.name != null)
      fileName.append(".").append(this.name);
    fileName.append(".txt");

    String fileDirRoot = conf.getString(Keys.LOGGER_FILE_DIR);

    synchronized (this) {
      try {
        if (fileDirRoot == null) {
          System.out.println(text);
        } else {
          // check dir
          Path dirPath = Paths.get(fileDirRoot, date);
          if (!Files.exists(dirPath))
            Files.createDirectories(dirPath);

          // write
          Path filePath = Paths.get(fileDirRoot, date, fileName.toString());
          if (!Files.exists(filePath))
            Files.createFile(filePath);
          Files.write(filePath, text.getBytes(), StandardOpenOption.APPEND);
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void appendStackTrace(StringBuilder buf, Throwable t) {
    StackTraceElement[] trace = t.getStackTrace();
    for (StackTraceElement element : trace) {
      buf.append("\tat ").append(element).append("\n");
    }

    Throwable cause = t.getCause();
    if (cause != null) {
      buf.append("Cause: ").append("\n");
      buf.append(cause.getClass().getName()).append(" : ").append(cause.getMessage()).append("\n");
    }
  }

  protected static int getFileLevel() {
    return getLevel(conf.getString(Keys.LOGGER_FILE_LEVEL, "ERROR"));
  }

  protected static String getFileName(int level) {
    if (LOG_LEVEL_WARN > level)
      return "log";
    if (LOG_LEVEL_WARN < level)
      return "error";
    return "warn";
  }

  public Logger trace(Object message) {
    log(LOG_LEVEL_TRACE, message, null);
    return this;
  }

  public Logger trace(Object message, Throwable t) {
    log(LOG_LEVEL_TRACE, message, t);
    return this;
  }

  public Logger debug(Object message) {
    log(LOG_LEVEL_DEBUG, message, null);
    return this;
  }

  public Logger debug(Object message, Throwable t) {
    log(LOG_LEVEL_DEBUG, message, t);
    return this;
  }

  public Logger info(String message, boolean forcedSysout) {
    if (sysout || forcedSysout)
      System.out.println(message);
    log(LOG_LEVEL_INFO, message, null);
    return this;
  }

  public Logger info(Object message) {
    if (sysout)
      System.out.println(message);
    log(LOG_LEVEL_INFO, message, null);
    return this;
  }

  public Logger info(Object message, Throwable t) {
    if (sysout)
      System.out.println(message);
    log(LOG_LEVEL_INFO, message, t);
    return this;
  }

  public Logger warn(Object message) {
    log(LOG_LEVEL_WARN, message, null);
    return this;
  }

  public Logger warn(Object message, Throwable t) {
    log(LOG_LEVEL_WARN, message, t);
    return this;
  }

  public Logger error(Object message) {
    log(LOG_LEVEL_ERROR, message, null);
    return this;
  }

  public Logger error(Object message, Throwable t) {
    log(LOG_LEVEL_ERROR, message, t);
    return this;
  }

  public Logger fatal(Object message) {
    log(LOG_LEVEL_FATAL, message, null);
    return this;
  }

  public Logger fatal(Object message, Throwable t) {
    log(LOG_LEVEL_FATAL, message, t);
    return this;
  }

  private static int getLevel(String lvl) {
    if ("all".equalsIgnoreCase(lvl)) {
      return LOG_LEVEL_ALL;
    } else if ("trace".equalsIgnoreCase(lvl)) {
      return LOG_LEVEL_TRACE;
    } else if ("debug".equalsIgnoreCase(lvl)) {
      return LOG_LEVEL_DEBUG;
    } else if ("info".equalsIgnoreCase(lvl)) {
      return LOG_LEVEL_INFO;
    } else if ("warn".equalsIgnoreCase(lvl)) {
      return LOG_LEVEL_WARN;
    } else if ("error".equalsIgnoreCase(lvl)) {
      return LOG_LEVEL_ERROR;
    } else if ("fatal".equalsIgnoreCase(lvl)) {
      return LOG_LEVEL_FATAL;
    }
    return LOG_LEVEL_OFF;
  }

  private static String getLevelName(int level) {
    switch (level) {
      case LOG_LEVEL_TRACE:
        return "TRACE";
      case LOG_LEVEL_DEBUG:
        return "DEBUG";
      case LOG_LEVEL_INFO:
        return "INFO";
      case LOG_LEVEL_WARN:
        return "WARN";
      case LOG_LEVEL_ERROR:
        return "ERROR";
      case LOG_LEVEL_FATAL:
        return "FATAL";
    }
    return null;
  }
}
