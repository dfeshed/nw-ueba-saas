package fortscale.monitoring.external.stats.linux.collector;

import fortscale.monitoring.external.stats.linux.collector.collectors.ExternalStatsFileSystemCollector;
import fortscale.monitoring.external.stats.linux.collector.metrics.ExternalStatsFileSystemCollectorMetrics;
import fortscale.utils.system.FileSystemUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;

/**
 * Created by galiar on 02/05/2016.
 */
public class ExternalStatsFileSystemCollectorTest {

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

    }

    /**
     *  Test the file system collector - that the total space. free space and used space are calculated correctly
     *  in order to test it, mock a file system.
     *
     */
    @Test
    public void testExternalStatsFileSystemCollector() throws Exception{

        File mockFile = Mockito.mock(File.class);
        FileSystemUtils mockFileSystemUtils = Mockito.mock(FileSystemUtils.class);
        Mockito.when(mockFile.getName()).thenReturn("src");
        Mockito.when(mockFileSystemUtils.getFreeSpace(Mockito.anyString())).thenReturn(1745265704960L);
        Mockito.when(mockFileSystemUtils.getTotalSpace(Mockito.anyString())).thenReturn(2000395694080L);


        ExternalStatsFileSystemCollector fileSystemCollector = new ExternalStatsFileSystemCollector(mockFile.getName());
        fileSystemCollector.setFileSystemUtils(mockFileSystemUtils);

        fileSystemCollector.collect(null);
        ExternalStatsFileSystemCollectorMetrics fileSystemCollectorMetrics = fileSystemCollector.getFileSystemMetrics();

        Assert.assertEquals(1907725,fileSystemCollectorMetrics.getTotalFileSystemSize().longValue());
        Assert.assertEquals(1664415,fileSystemCollectorMetrics.getFreeSpace().longValue());
        Assert.assertEquals(243310,fileSystemCollector.getFileSystemMetrics().getUsedSpace().longValue());


    }

}
