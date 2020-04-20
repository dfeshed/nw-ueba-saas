package fortscale.services.presidio.converter;

import fortscale.domain.core.Severity;
import fortscale.services.presidio.core.converters.EnumConverter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import presidio.output.client.model.Alert;
import presidio.output.client.model.AlertQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shays on 11/09/2017.
 */
public class EnumConverterTest {

    private EnumConverter<Alert.SeverityEnum,Severity,AlertQuery.SeverityEnum> severityConverter;

    @Before
    public void setUp(){
        Map<Alert.SeverityEnum,Severity> queryEnumToUiEnum = new HashMap<>();
        queryEnumToUiEnum.put(Alert.SeverityEnum.CRITICAL,Severity.Critical);
        queryEnumToUiEnum.put(Alert.SeverityEnum.HIGH,Severity.High);
        queryEnumToUiEnum.put(Alert.SeverityEnum.MEDIUM,Severity.Medium);
        queryEnumToUiEnum.put(Alert.SeverityEnum.LOW ,Severity.Low);



        severityConverter = EnumConverter.createInstance(queryEnumToUiEnum,AlertQuery.SeverityEnum.class);
    }

    @Test
    public void convertSeverityToQuery(){
        List<AlertQuery.SeverityEnum> presidioCoreInstance = severityConverter.convertUiFilterToQueryDto("CRITICAL,HIGH");
        Assert.assertEquals(2,presidioCoreInstance.size());

        AlertQuery.SeverityEnum value = presidioCoreInstance.get(0);
        Assert.assertTrue(presidioCoreInstance.contains(AlertQuery.SeverityEnum.CRITICAL));
        Assert.assertTrue(presidioCoreInstance.contains(AlertQuery.SeverityEnum.HIGH));
    }

    @Test
    public void convertSeverityToQueryEmptyString(){
        List<AlertQuery.SeverityEnum> presidioCoreInstance = severityConverter.convertUiFilterToQueryDto("");
        Assert.assertEquals(0,presidioCoreInstance.size());

        List<AlertQuery.SeverityEnum> presidioCoreInstance2 = severityConverter.convertUiFilterToQueryDto(null);
        Assert.assertEquals(0,presidioCoreInstance2.size());



    }

    @Test
    public void convertSeverityToQueryLowerCase(){
        List<AlertQuery.SeverityEnum> presidioCoreInstance = severityConverter.convertUiFilterToQueryDto("critical,high");
        Assert.assertEquals(2,presidioCoreInstance.size());

        AlertQuery.SeverityEnum value = presidioCoreInstance.get(0);
        Assert.assertTrue(presidioCoreInstance.contains(AlertQuery.SeverityEnum.CRITICAL));
        Assert.assertTrue(presidioCoreInstance.contains(AlertQuery.SeverityEnum.HIGH));
    }


    @Test
    public void convertResponseToUiSeverity(){
        Severity s=severityConverter.convertResponseToUiDto(Alert.SeverityEnum.CRITICAL);
        Assert.assertEquals(Severity.Critical,s);
    }

    @Test
    public void convertResponseToUiSeverityNull(){
        Severity s=severityConverter.convertResponseToUiDto(null);
        Assert.assertEquals(null,s);
    }

}
