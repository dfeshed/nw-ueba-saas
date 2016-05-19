package fortscale.ml.model;

import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.builder.IModelBuilderConf;
import fortscale.ml.model.listener.IModelBuildingListener;
import fortscale.ml.model.listener.ModelBuildingStatus;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.AbstractDataRetrieverConf;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.ml.model.selector.IContextSelectorConf;
import fortscale.ml.model.store.ModelStore;
import fortscale.utils.factory.FactoryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class ModelBuilderManagerTest {
    @Configuration
    @ImportResource("classpath*:META-INF/spring/model-builder-manager-test-context.xml")
    static class ContextConfiguration {
        @Bean
        public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
            Properties properties = new Properties();
            properties.put("fortscale.model.build.selector.delta.in.seconds", 604800);
            PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
            configurer.setProperties(properties);
            return configurer;
        }
    }

    private static final String DEFAULT_SESSION_ID = "testSessionId";

    @Autowired
    private FactoryService factoryService;
    @Autowired
    private ModelStore store;

    private ModelConf modelConf;
    private IContextSelector selector;
    private AbstractDataRetriever retriever;
    private IModelBuilder builder;

    @Before
    public void setUp() {
        modelConf = mock(ModelConf.class);
        reset(factoryService);

        selector = mock(IContextSelector.class);
        retriever = mock(AbstractDataRetriever.class);
        builder = mock(IModelBuilder.class);

        AbstractDataRetrieverConf retrieverConf = mock(AbstractDataRetrieverConf.class);
        when(modelConf.getDataRetrieverConf()).thenReturn(retrieverConf);
        when(factoryService.getProduct(eq(retrieverConf))).thenReturn(retriever);

        IModelBuilderConf builderConf = mock(IModelBuilderConf.class);
        when(modelConf.getModelBuilderConf()).thenReturn(builderConf);
        when(factoryService.getProduct(eq(builderConf))).thenReturn(builder);

        reset(store);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfConstructedWithoutModelConf() {
        new ModelBuilderManager(null);
    }

    @Test
    public void shouldBuildAndStoreModelsForAllSelectedEntities() {
        Date previousEndTime = new Date(1419552000000L);
        Date currentStartTime = new Date(1420156800000L);
        Date currentEndTime = new Date(1420156800000L);
        String[] ids = {"user1", "user2"};
        Model[] models = {mock(Model.class), mock(Model.class)};
        boolean[] successes = {true, true};

        ModelBuilderManager manager = createProcessScenario(previousEndTime, currentStartTime, currentEndTime, ids, models, successes);
        manager.process(null, DEFAULT_SESSION_ID, previousEndTime, currentEndTime);

        verify(selector).getContexts(eq(previousEndTime), eq(currentEndTime));
        verify(builder, times(ids.length)).build(any());
        for (int i = 0; i < ids.length; i++) {
            verify(retriever).retrieve(eq(ids[i]), eq(currentEndTime));
            verify(store).save(eq(modelConf), eq(DEFAULT_SESSION_ID), eq(ids[i]), eq(models[i]), eq(currentStartTime), eq(currentEndTime));
        }

        verifyNoMoreInteractions(selector, retriever, builder, store);
    }

    @Test
    public void shouldBuildAndStoreGlobalModel() {
        Date currentStartTime = new Date(1420156800000L);
        Date currentEndTime = new Date(1420156800000L);
        Model[] models = {mock(Model.class)};
        boolean[] successes = {true};

        ModelBuilderManager manager = createProcessScenario(null, currentStartTime, currentEndTime, null, models, successes);
        manager.process(null, DEFAULT_SESSION_ID, null, currentEndTime);

        verify(retriever).retrieve(isNull(String.class), eq(currentEndTime));
        verify(builder).build(any());
        verify(store).save(eq(modelConf), eq(DEFAULT_SESSION_ID), isNull(String.class), eq(models[0]), eq(currentStartTime), eq(currentEndTime));

        verifyNoMoreInteractions(selector, retriever, builder, store);
    }

    @Test
    public void shouldInformListenerOnModelBuildingStatus() {
        String modelConfName = "testModelConf";
        when(modelConf.getName()).thenReturn(modelConfName);

        Date previousEndTime = new Date(1419552000000L);
        Date currentStartTime = new Date(1420156800000L);
        Date currentEndTime = new Date(1420156800000L);
        String[] ids = {"user1", "user2"};
        Model[] models = {mock(Model.class), mock(Model.class)};
        boolean[] successes = {true, false};

        IModelBuildingListener listener = mock(IModelBuildingListener.class);
        ModelBuilderManager manager = createProcessScenario(previousEndTime, currentStartTime, currentEndTime, ids, models, successes);
        manager.process(listener, DEFAULT_SESSION_ID, previousEndTime, currentEndTime);

        for (int i = 0; i < ids.length; i++) {
            ModelBuildingStatus status = successes[i] ? ModelBuildingStatus.SUCCESS : ModelBuildingStatus.STORE_FAILURE;
            verify(listener).modelBuildingStatus(
                    eq(modelConfName), eq(DEFAULT_SESSION_ID), eq(ids[i]), eq(currentEndTime), eq(status));
        }

        verify(listener).modelBuildingSummary(eq(modelConfName), eq(DEFAULT_SESSION_ID), eq(currentEndTime), eq(1L), eq(1L));
        verifyNoMoreInteractions(listener);
    }

    private void mockBuild(String id, Date startTime, Date endTime, Model model, boolean success) {
        Object data = mock(Object.class);
        when(retriever.retrieve(eq(id), eq(endTime))).thenReturn(data);
        when(builder.build(eq(data))).thenReturn(model);

        if (!success) {
            doThrow(Exception.class).when(store).save(eq(modelConf), eq(DEFAULT_SESSION_ID), eq(id), eq(model), eq(startTime), eq(endTime));
        }
    }

    private ModelBuilderManager createProcessScenario(
            Date previousEndTime, Date currentStartTime, Date currentEndTime, String[] ids, Model[] models, boolean[] successes) {

        if (ids != null) {
            IContextSelectorConf selectorConf = mock(IContextSelectorConf.class);
            when(modelConf.getContextSelectorConf()).thenReturn(selectorConf);
            when(factoryService.getProduct(eq(selectorConf))).thenReturn(selector);
            when(selector.getContexts(eq(previousEndTime), eq(currentEndTime))).thenReturn(Arrays.asList(ids));

            for (int i = 0; i < ids.length; i++) {
                mockBuild(ids[i], currentStartTime, currentEndTime, models[i], successes[i]);
            }
        } else {
            mockBuild(null, currentStartTime, currentEndTime, models[0], successes[0]);
        }

        return new ModelBuilderManager(modelConf);
    }
}
