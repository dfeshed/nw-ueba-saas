package com.rsa.netwitness.presidio.automation.domain.filter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "input_filtered_raw_events")
public class AdaprerFilteredRawEventStoredData {
    @Id
    private String id;

    private List<ResponseMongoDB> violations;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ResponseMongoDB> getViolations() {
        return violations;
    }

    public void setViolations(List<ResponseMongoDB> violations) {
        this.violations = violations;
    }

    public class ResponseMongoDB {
        String interpolatedMessage;

        String propertyPath;

        String rootBeanClass;

        String messageTemplate;

        public String getInterpolatedMessage() {
            return interpolatedMessage;
        }

        public void setInterpolatedMessage(String interpolatedMessage) {
            this.interpolatedMessage = interpolatedMessage;
        }

        public String getPropertyPath() {
            return propertyPath;
        }

        public void setPropertyPath(String propertyPath) {
            this.propertyPath = propertyPath;
        }

        public String getRootBeanClass() {
            return rootBeanClass;
        }

        public void setRootBeanClass(String rootBeanClass) {
            this.rootBeanClass = rootBeanClass;
        }

        public String getMessageTemplate() {
            return messageTemplate;
        }

        public void setMessageTemplate(String messageTemplate) {
            this.messageTemplate = messageTemplate;
        }

        @Override
        public String toString() {
            return "ResponseMongoDB{" +
                    "interpolatedMessage='" + interpolatedMessage + '\'' +
                    ", propertyPath='" + propertyPath + '\'' +
                    ", rootBeanClass='" + rootBeanClass + '\'' +
                    ", messageTemplate='" + messageTemplate + '\'' +
                    '}';
        }
    }
}
