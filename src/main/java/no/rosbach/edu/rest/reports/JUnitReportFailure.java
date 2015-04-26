package no.rosbach.edu.rest.reports;

import org.junit.ComparisonFailure;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

/**
 * Created by mapster on 26.04.15.
 */
public class JUnitReportFailure {
    private String testClassName;
    private String testMethodName;
    private String failureType;
    private String expected;
    private String actual;

    /**
     * The default constructor.
     */
    public JUnitReportFailure() {

    }

    /**
     * Construct a JUnitReportFailure from a Failure.
     * @param f the failure.
     */
    public JUnitReportFailure(Failure f) {
        Description d = f.getDescription();
        this.testClassName = d.getClassName();
        this.testMethodName = d.getMethodName();

        Throwable exception = f.getException();
        this.failureType = exception.getClass().getSimpleName();
        if(exception instanceof ComparisonFailure) {
            ComparisonFailure cmpFailure = (ComparisonFailure) exception;
            this.expected = cmpFailure.getExpected();
            this.actual = cmpFailure.getActual();
        }
    }

    /**
     * The name of the test class where the test failed.
     * @return class name.
     */
    public String getTestClassName() {
        return testClassName;
    }

    /**
     * The name of the test method that failed.
     * @return method name.
     */
    public String getTestMethodName() {
        return testMethodName;
    }

    /**
     * The kind of failure, e.g. ({@link AssertionError} or {@link ComparisonFailure}.
     * @return simple class name of failure.
     */
    public String getFailureType() {
        return failureType;
    }

    /**
     * The expected value of a comparison test (assertEquals). The failure must be of type {@link ComparisonFailure}, otherwise null is returned.
     * @return null or the expected value.
     */
    public String getExpected() {
        return expected;
    }

    /**
     * The actual value of a comparison test (assertEquals). The failure must be of type {@link ComparisonFailure}, otherwise null is returned.
     * @return null or the actual value.
     */
    public String getActual() {
        return actual;
    }

    /**
     * Set the test class name.
     * @param testClassName the test class name.
     */
    public void setTestClassName(String testClassName) {
        this.testClassName = testClassName;
    }

    /**
     * Set the test method name.
     * @param testMethodName the test method name.
     */
    public void setTestMethodName(String testMethodName) {
        this.testMethodName = testMethodName;
    }

    /**
     * Set the failure type, e.g. {@link AssertionError}, {@link ComparisonFailure}.
     * @param failureType the failure type.
     */
    public void setFailureType(String failureType) {
        this.failureType = failureType;
    }

    /**
     * Set the expected value for a comparison failure.
     * @param expected the expected value.
     */
    public void setExpected(String expected) {
        this.expected = expected;
    }

    /**
     * Set the actual value for a comparison failure.
     * @param actual the actual value.
     */
    public void setActual(String actual) {
        this.actual = actual;
    }
}
