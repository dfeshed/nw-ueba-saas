package fortscale.web.rest.errorhandler;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shays on 07/05/2016.
 */

@ControllerAdvice
public class RestErrorHandler {

    /**
     * BindException thrown when spring validation find that the request is not met with the validation condition
     * In this case, this method catch the exception for all rest api(s) and return response with list of validation violation-
     * which property was wrong and why
     *
     * @param ex
     * @return
     */

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public ErrorMessagesCollection handleException(BindException ex) {

        List<ErrorMessage> errors = new ArrayList<>();
        for (ObjectError springValidationError : ex.getAllErrors()){
            ErrorMessage errorMessage = new ErrorMessage();
            if (springValidationError instanceof FieldError) {
                errorMessage.setAttribute(((FieldError) springValidationError).getField());//The name of the attribute from the object
                errorMessage.setMessage(springValidationError.getDefaultMessage());
            }
            else{
                errorMessage.setAttribute(springValidationError.getObjectName()); //The name of the object from the method
                errorMessage.setMessage(springValidationError.getDefaultMessage());
            }
            errors.add(errorMessage);
        }



        return new ErrorMessagesCollection(errors);
    }

}
