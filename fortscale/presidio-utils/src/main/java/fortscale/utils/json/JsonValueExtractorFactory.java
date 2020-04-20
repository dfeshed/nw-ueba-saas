package fortscale.utils.json;

public class JsonValueExtractorFactory {

    public IJsonValueExtractor create(Object extractParam){
        IJsonValueExtractor ret;
        if(extractParam != null && extractParam instanceof String && ((String)extractParam).startsWith("${") && ((String)extractParam).endsWith("}")) {
            String pointerPath = ((String)extractParam).substring(2, ((String)extractParam).length() - 1);
            ret = new JsonPointerValueExtractor(pointerPath);
        } else {
            ret = new JsonValueStaticExtractor(extractParam);
        }

        return ret;
    }
}
