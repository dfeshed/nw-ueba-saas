package fortscale.domain.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

import org.junit.Assert;
import org.junit.Test;

import fortscale.domain.fe.VpnScore;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.properties.IllegalStructuredProperty;
import fortscale.utils.properties.PropertyNotExistException;

public class ImpalaResultSetToVpnScoreItemConverterTest {

	@Test
	public void simpleConvertion() throws InstantiationException, IllegalAccessException, PropertyNotExistException, IllegalStructuredProperty, SQLException, InvocationTargetException, NoSuchMethodException, ParseException{						
		ResultSet rs = mock(ResultSet.class) ;
		String runtimeField = "runtime";
		String timestampValue = "1389632400";
		when(rs.getObject(runtimeField)).thenReturn(timestampValue);
		String dateTimeField = "date_time";
		String dateTimeValue = "2001-07-01 20:31:09";
		when(rs.getString(dateTimeField)).thenReturn(dateTimeValue);
		String normalizedUsernameField = "normalized_username";
		String normalizedUsernameVal = "normalized_username_value";
		when(rs.getObject(normalizedUsernameField)).thenReturn(normalizedUsernameVal);
		String dateTimeScoreField = "date_timescore";
		Double dateTimeScoreValue = 11.5D;
		when(rs.getObject(dateTimeScoreField)).thenReturn(dateTimeScoreValue);
		
		ImpalaResultSetToBeanItemConverter<VpnScore> converter = new ImpalaResultSetToBeanItemConverter<VpnScore>(new VpnScore());
		
		VpnScore vpnScore = new VpnScore();
		converter.convert(rs, vpnScore);
		
		ImpalaParser impalaParser = new ImpalaParser();
		Assert.assertEquals(impalaParser.parseTimeDate(dateTimeValue), vpnScore.getDate_time());
		Assert.assertEquals(normalizedUsernameVal, vpnScore.getNormalized_username());
		Assert.assertEquals(dateTimeScoreValue, vpnScore.getDate_timeScore());
	}
}
