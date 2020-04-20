package presidio.output.processor.services.alert.supportinginformation.transformer;

public interface SupportingInformationTransformerFactory {

    SupportingInformationTransformer getTransformer(String type);

}
