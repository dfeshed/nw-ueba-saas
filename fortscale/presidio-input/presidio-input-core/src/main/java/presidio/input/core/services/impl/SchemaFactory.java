package presidio.input.core.services.impl;

import presidio.input.core.services.converters.ade.InputAdeConverter;
import presidio.input.core.services.converters.output.InputOutputConverter;
import presidio.input.core.services.transformation.managers.TransformationManager;

public interface SchemaFactory {
    TransformationManager getTransformationManager(String schema);

    InputAdeConverter getInputToAdeConverter(String schema);

    InputOutputConverter getInputToOutputConverter(String schema);
}
