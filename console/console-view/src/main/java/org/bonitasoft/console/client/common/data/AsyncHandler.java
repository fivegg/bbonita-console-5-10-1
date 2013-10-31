package org.bonitasoft.console.client.common.data;

/**
 * @author Nicolas Chabanoles
 *
 * @param <T>
 */
public interface AsyncHandler<T> {

  /**
 * @param t
 */
void handleFailure(Throwable t);
  /**
 * @param result
 */
void handleSuccess(T result);
  
}
