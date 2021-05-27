/**
 * Copyright 2009-2018 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ibatis.reflection.invoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.ibatis.reflection.Reflector;

/**
 * 中文注释: 负责对象其他方法的操作
 *
 * @author Clinton Begin
 */
public class MethodInvoker implements Invoker {

  // 传入参数或者传出参数类型
  private final Class<?> type;
  private final Method method;

  /**
   * MethodInvoker构造方法
   *
   * @param method 方法
   */
  public MethodInvoker(Method method) {
    this.method = method;

    if (method.getParameterTypes().length == 1) {
      // 如果方法的形参列表长度为1，表示当前方法是set方法，故其type为形参的类型
      type = method.getParameterTypes()[0];
    } else {
      // 形参列表不为1则为get方法，其type为方法返回值类型
      type = method.getReturnType();
    }
  }

  @Override
  public Object invoke(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException {
    try {
      // 执行调用，method.invoke(target, args)为被适配的点
      return method.invoke(target, args);
    } catch (IllegalAccessException e) {
      // 如果无法访问，则表示当前方法是private修饰的
      // 于是mybaits会查看默认的安全管理器配置文件  在$JAVA_HOME/jre/lib/security/java.policy中
      // suppressAccessChecks的值，用于判断是否可以使用setAccessible方法进行强制调用
      if (Reflector.canControlMemberAccessible()) {
        method.setAccessible(true);
        return method.invoke(target, args);
      } else {
        throw e;
      }
    }
  }

  @Override
  public Class<?> getType() {
    return type;
  }
}
