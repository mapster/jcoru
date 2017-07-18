package no.rosbach.jcoru.rest.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Error controller that maps web application exceptions to json.
 */
@RestController
@RequestMapping("/error")
public class JsonErrorController implements ErrorController {

    private static final String TRACE_PARAMETER = "trace";
    private final ErrorAttributes errorAttributes;

    @Autowired
    public JsonErrorController(ErrorAttributes errorAttributes) {
        Assert.notNull(errorAttributes, "ErrorAttributes must not be null");
        this.errorAttributes = errorAttributes;
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> error(HttpServletRequest aRequest){
        return getErrorAttributes(aRequest, getTraceParameter(aRequest));
    }

    private boolean getTraceParameter(HttpServletRequest request) {
        String parameter = request.getParameter(TRACE_PARAMETER);
        if (parameter == null) {
            return false;
        }
        return !"false".equalsIgnoreCase(parameter);
    }

    private Map<String, Object> getErrorAttributes(HttpServletRequest aRequest, boolean includeStackTrace) {
        RequestAttributes requestAttributes = new ServletRequestAttributes(aRequest);
        return errorAttributes.getErrorAttributes(requestAttributes, includeStackTrace);
    }
}
