public class AggregationClass implements no.rosbach.jcoru.compile.fixtures.AggregationClass {
  ContainedClass value = new ContainedClass();

  public ContainedClass getValue() {
    return value;
  }

  public String getContainedValue() {
    return value.getActualValue();
  }
}