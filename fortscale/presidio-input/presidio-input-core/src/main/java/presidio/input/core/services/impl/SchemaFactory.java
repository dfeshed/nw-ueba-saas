package presidio.input.core.services.impl;

import fortscale.common.general.Schema;
import presidio.input.core.services.converters.inputtoade.ActiveDirectoryConverter;
import presidio.input.core.services.converters.inputtoade.AuthenticationConverter;
import presidio.input.core.services.converters.inputtoade.FileConverter;
import presidio.input.core.services.converters.inputtoade.InputAdeConverter;
import presidio.input.core.services.converters.inputtooutput.ActiveDirectoryInputToOutputConverter;
import presidio.input.core.services.converters.inputtooutput.AuthenticationInputToOutputConverter;
import presidio.input.core.services.converters.inputtooutput.FileInputToOutputConverter;
import presidio.input.core.services.converters.inputtooutput.InputOutputConverter;
import presidio.input.core.services.transformation.managers.ActiveDirectoryTransformationManager;
import presidio.input.core.services.transformation.managers.AuthenticationTransformerManager;
import presidio.input.core.services.transformation.managers.FileTransformerManager;
import presidio.input.core.services.transformation.managers.TransformationManager;

public class SchemaFactory {
    public static TransformationManager getTransformationManager(Schema schema) {

        if (Schema.FILE.equals(schema))
            return new FileTransformerManager();
        else if (Schema.ACTIVE_DIRECTORY.equals(schema)) {
            return new ActiveDirectoryTransformationManager();
        } else if (Schema.AUTHENTICATION.equals(schema)) {
            return new AuthenticationTransformerManager();
        } else {
            throw new UnsupportedOperationException("Unsupported data source " + schema);
        }
    }

    public static InputAdeConverter getInputAdeConverter(Schema schema) {

        if (Schema.FILE.equals(schema))
            return new FileConverter();
        else if (Schema.ACTIVE_DIRECTORY.equals(schema)) {
            return new ActiveDirectoryConverter();
        } else if (Schema.AUTHENTICATION.equals(schema)) {
            return new AuthenticationConverter();
        } else {
            throw new UnsupportedOperationException("Unsupported data source " + schema);
        }
    }

    public static InputOutputConverter getInputOutputConverter(Schema schema) {

        if (Schema.FILE.equals(schema))
            return new FileInputToOutputConverter();
        else if (Schema.ACTIVE_DIRECTORY.equals(schema)) {
            return new ActiveDirectoryInputToOutputConverter();
        } else if (Schema.AUTHENTICATION.equals(schema)) {
            return new AuthenticationInputToOutputConverter();
        } else {
            throw new UnsupportedOperationException("Unsupported data source " + schema);
        }
    }

}
