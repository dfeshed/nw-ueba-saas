package fortscale.services.impl;

import fortscale.domain.core.Alert;
import fortscale.domain.core.Severity;
import fortscale.domain.dto.DailySeveiryConuntDTO;
import fortscale.domain.dto.DateRange;
import fortscale.services.AlertsService;
import fortscale.services.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by shays on 24/05/2016.
 */

@RunWith(MockitoJUnitRunner.class)
public class AlertServiceProxyTest {

   @Test
   public void testProxy(){
       AlertsServiceImpl a = new AlertsServiceImpl();
       a.init();
   }
}
