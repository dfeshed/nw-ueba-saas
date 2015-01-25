package fortscale.flume;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;
import org.apache.flume.interceptor.RegexExtractorInterceptorPassThroughSerializer;
import org.apache.flume.interceptor.RegexExtractorInterceptorSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HeaderRegexExtractorFilterInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(HeaderRegexExtractorFilterInterceptor.class);

    private final ArrayList<Pattern> regexes;
    private final List<NameAndSerializer> serializers;
    private final ArrayList<String> headersList;

    private HeaderRegexExtractorFilterInterceptor(ArrayList<Pattern> regexes, List<NameAndSerializer> serializers, ArrayList<String> headersList) {
        this.regexes = regexes;
        this.serializers = serializers;
        this.headersList = headersList;
    }

    @Override
    public void initialize() {}

    @Override
    public void close() {}

    @Override
    public Event intercept(Event event) {
        int regexCounter = 0;
        String eventBody = new String(event.getBody(), Charsets.UTF_8);
        for (Pattern regex : regexes){
            Matcher matcher = regex.matcher(eventBody);
            Map<String, String> headers = event.getHeaders();
            if (matcher.find()) {
                for (int group = 0, count = matcher.groupCount(); group < count; group++) {
                    int groupIndex = group + 1;
                    if (groupIndex > serializers.size()) {
                        break;
                    }
                    NameAndSerializer serializer = serializers.get(group);

                    headers.put(serializer.headerName, headersList.get(regexCounter));
                    return event;
                }
            }
            regexCounter++;
        }
        return null;
    }

    @Override
    public List<Event> intercept(List<Event> events) {
        List<Event> intercepted = Lists.newArrayListWithCapacity(events.size());
        for (Event event : events) {
            Event interceptedEvent = intercept(event);
            if (interceptedEvent != null) {
                intercepted.add(interceptedEvent);
            }
        }
        return intercepted;
    }

    public static class Builder implements Interceptor.Builder {

        static final String REGEX = "regex";
        static final String SERIALIZERS = "serializers";

        private ArrayList<Pattern> regexes = new ArrayList<Pattern>();
        private List<NameAndSerializer> serializerList;
        private final RegexExtractorInterceptorSerializer defaultSerializer = new RegexExtractorInterceptorPassThroughSerializer();
        private ArrayList<String> headersList;

        @Override
        public void configure(Context context) {
            String regexString = context.getString(REGEX);
            Preconditions.checkArgument(StringUtils.isNotEmpty(regexString), "Must supply a valid regex string");
            String[] regexesArray = regexString.split("#+");
            configureSerializers(context);

            for (String singleRegex : regexesArray)
                regexes.add(Pattern.compile(singleRegex));

            // Parse Headers from config
            String[] headersArray = context.getString("headersToPut").split("\\s+");
            headersList = new ArrayList<String>(Arrays.asList(headersArray));
        }

        private void configureSerializers(Context context) {
            String serializerListStr = context.getString(SERIALIZERS);
            Preconditions.checkArgument(!StringUtils.isEmpty(serializerListStr), "Must supply at least one name and serializer");

            String[] serializerNames = serializerListStr.split("\\s+");
            Context serializerContexts = new Context(context.getSubProperties(SERIALIZERS + "."));

            serializerList = Lists.newArrayListWithCapacity(serializerNames.length);
            for(String serializerName : serializerNames) {
                Context serializerContext = new Context(
                        serializerContexts.getSubProperties(serializerName + "."));
                String type = serializerContext.getString("type", "DEFAULT");
                String name = serializerContext.getString("name");
                Preconditions.checkArgument(!StringUtils.isEmpty(name), "Supplied name cannot be empty.");

                if("DEFAULT".equals(type)) {
                    serializerList.add(new NameAndSerializer(name, defaultSerializer));
                } else {
                    serializerList.add(new NameAndSerializer(name, getCustomSerializer(type, serializerContext)));
                }
            }
        }

        private RegexExtractorInterceptorSerializer getCustomSerializer(String clazzName, Context context) {
            try {
                RegexExtractorInterceptorSerializer serializer = (RegexExtractorInterceptorSerializer)
                        Class.forName(clazzName).newInstance();
                serializer.configure(context);
                return serializer;
            } catch (Exception e) {
                logger.error("Could not instantiate event serializer.", e);
                Throwables.propagate(e);
            }
            return defaultSerializer;
        }

        @Override
        public Interceptor build() {
            Preconditions.checkArgument(serializerList.size() > 0, "Must supply a valid group match id list");
            return new HeaderRegexExtractorFilterInterceptor(regexes, serializerList,headersList);
        }
    }

    static class NameAndSerializer {
        private final String headerName;
        private final RegexExtractorInterceptorSerializer serializer;


        public NameAndSerializer(String headerName, RegexExtractorInterceptorSerializer serializer) {
            this.headerName = headerName;
            this.serializer = serializer;
        }
    }
}
