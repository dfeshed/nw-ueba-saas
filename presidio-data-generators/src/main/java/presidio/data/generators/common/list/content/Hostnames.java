package presidio.data.generators.common.list.content;

import com.google.common.collect.ImmutableList;

import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static presidio.data.generators.common.list.content.AlexaDomains.ALEXA_DOMAINS;
import static presidio.data.generators.common.list.content.SingleWord.SINGLE_WORDS;

public class Hostnames {

    public static ImmutableList<String> HOSTNAMES = ImmutableList.copyOf(
                IntStream.range(0, ALEXA_DOMAINS.size()).boxed()
                        .map(index -> SINGLE_WORDS.get(index + 1000).concat(".").concat(ALEXA_DOMAINS.get(index)))
                        .collect(toList()));
}
