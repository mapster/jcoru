
public class AggregationClass implements no.rosbach.edu.compiler.fixtures.AggregationClass {
    ContainedClass value = new ContainedClass();

    public ContainedClass getValue() {
        return value;
    }

    public String getContainedValue() {
        return value.getActualValue();
    }
}