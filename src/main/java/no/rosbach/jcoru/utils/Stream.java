package no.rosbach.jcoru.utils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.StreamSupport;

/**
 * Created by mapster on 26.04.15.
 */
public class Stream {
  private Stream() {
  }

  public static <T> java.util.stream.Stream<T> stream(Iterator<T> iterator) {
    Iterable<T> iterable = () -> iterator;
    return StreamSupport.stream(iterable.spliterator(), false);
  }

  public static <T> java.util.stream.Stream<T> stream(Iterable<T> iterable) {
    return StreamSupport.stream(iterable.spliterator(), false);
  }

  public static <T> java.util.stream.Stream<T> stream(T[] array) {
    return StreamSupport.stream(Arrays.spliterator(array), false);
  }
}
