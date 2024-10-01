package com.example.grpc.api.common.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
public class MyData extends CacheData<MyDataObject> {
  @Override
  protected Status load0() {
    final MyDataObject object = new MyDataObject();
    try {
      object.add("test1", "hello world1");
      object.add("test2", "hello world2");
      object.add("test3", "hello world3");
      // FSやDBから読み込み処理を行いMyDataObjectを生成
      setObject(object);
      System.out.println("MyData set -> " + getActiveObject());
      return Status.SUCCESS;
    } catch (Exception e) {
      return Status.FAILURE;
    }
  }

  @Data
  @AllArgsConstructor
  public static class ProcessedData {
    private int dataId;
    private String value;
  }

  public ProcessedData getById(int dataId) {
    MyDataObject myDataObject = getActiveObject();
    for (String key : myDataObject.keySet()) {
      if (key.contains(String.valueOf(dataId))) {
        return new ProcessedData(dataId, myDataObject.get(key));
      }
    }
    return null;
  }
}
