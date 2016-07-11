package fortscale.web.rest.errorhandler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by shays on 07/05/2016.
 */
public class ErrorMessagesCollection {

        private List<ErrorMessage> errors;

        public ErrorMessagesCollection() {
        }

        public ErrorMessagesCollection(List<ErrorMessage> errors) {
            this.errors = errors;
        }

        public ErrorMessagesCollection(ErrorMessage error) {
            this(Collections.singletonList(error));
        }

        public ErrorMessagesCollection(ErrorMessage... errors) {
            this(Arrays.asList(errors));
        }

        public List<ErrorMessage> getErrors() {
            return errors;
        }

        public void setErrors(List<ErrorMessage> errors) {
            this.errors = errors;
        }

}
