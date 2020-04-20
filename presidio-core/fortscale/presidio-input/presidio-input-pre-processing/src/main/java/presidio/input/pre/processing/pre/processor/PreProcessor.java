package presidio.input.pre.processing.pre.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.Validate;

public abstract class PreProcessor<T> {
    private final String name;
    private final Class<T> argumentsClass;
    private final ObjectMapper objectMapper;

    /**
     * Constructor.
     *
     * @param name           The name of this pre-processor.
     * @param argumentsClass The class of the arguments that are passed to this pre-processor.
     */
    PreProcessor(String name, Class<T> argumentsClass) {
        this.name = Validate.notBlank(name, "name cannot be blank.");
        this.argumentsClass = Validate.notNull(argumentsClass, "argumentsClass cannot be null.");
        this.objectMapper = new ObjectMapper();
    }

    /**
     * @return The name of this pre-processor.
     */
    public String getName() {
        return name;
    }

    /**
     * Run this pre-processor with the given arguments. The application receives the arguments serialized as a JSON
     * string and should call this method with that string. The method deserializes the string into an instance of
     * {@link #argumentsClass} and calls the inheritor to do the actual pre-processing with that arguments instance.
     *
     * @param argumentsAsJsonString The arguments as a JSON string.
     */
    public void preProcess(String argumentsAsJsonString) {
        T arguments;

        try {
            arguments = objectMapper.readValue(argumentsAsJsonString, argumentsClass);
        } catch (Exception exception) {
            String format = "Could not deserialize arguments as JSON string to instance of %s: %s.";
            String message = String.format(format, argumentsClass.getSimpleName(), argumentsAsJsonString);
            throw new IllegalArgumentException(message, exception);
        }

        preProcess(arguments);
    }

    /**
     * Run this pre-processor with the given arguments. This is a package-private method that is called internally by
     * {@link #preProcess(String)} and should be implemented by the inheritor. This way, the inheritor does not need to
     * deserialize the arguments from a JSON string into an instance of {@link #argumentsClass}.
     *
     * @param arguments The arguments of the pre-processing.
     */
    abstract void preProcess(T arguments);
}
