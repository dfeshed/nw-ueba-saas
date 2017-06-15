package fortscale.utils.shell.testCommands;

import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;


@Component
public class CalculatorCommand implements CommandMarker {
    @CliAvailabilityIndicator({"calc"})
    public boolean isCommandAvailable() {
        return true;
    }

    @CliCommand(value = "calc", help = "sums two numbers")
    public String print(
            @CliOption(key = {"firstNumber"}, mandatory = true, help = "first number")
            final int number,
            @CliOption(key = {"operator"}, mandatory = true, help = "operator", specifiedDefaultValue="+")
            final String operator,
            @CliOption(key = {"secondNumber"}, mandatory = true, help = "second number")
            final int secondNumber
    ) {
        return String.format("executing: %d%s%d sum:%d",number,operator,secondNumber,number+secondNumber);
    }
}
