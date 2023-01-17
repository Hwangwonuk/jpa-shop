/*
 * Created by Wonuk Hwang on 2023/01/17
 * As part of Bigin
 *
 * Copyright (C) Bigin (https://bigin.io/main) - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by infra Team <wonuk_hwang@bigin.io>, 2023/01/17
 */
package jpabook.jpashop.exception;

/**
 * create on 2023/01/17. create by IntelliJ IDEA.
 *
 * <p> </p>
 * <p> {@link } and {@link } </p> *
 *
 * @author wonukHwang
 * @version 1.0
 * @see
 * @since (ex : 5 + 5)
 */
public class NotEnoughStockException extends RuntimeException {
  public NotEnoughStockException() {
    super();
  }
  public NotEnoughStockException(String message) {
    super(message);
  }
  public NotEnoughStockException(String message, Throwable cause) {
    super(message, cause);
  }
  public NotEnoughStockException(Throwable cause) {
    super(cause);
  }
}
