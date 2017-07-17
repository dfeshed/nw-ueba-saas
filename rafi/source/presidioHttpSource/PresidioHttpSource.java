package source.presidioHttpSource;

import com.google.common.base.Preconditions;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Server;

//import org.apache.catalina.deploy.FilterDef;
//import org.apache.catalina.deploy.FilterMap;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.startup.Tomcat;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDrivenSource;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.instrumentation.SourceCounter;
import org.apache.flume.source.AbstractSource;
import org.apache.flume.source.http.HTTPSourceConfigurationConstants;
import org.apache.flume.source.http.HTTPSourceHandler;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.jsp.JspMvcFeature;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//import org.mortbay.jetty.Server;

/**
 * Created by tomerd on 7/2/2017.
 */
//@Path("/api/v1.0")
public class PresidioHttpSource extends AbstractSource implements
        Configurable, EventDrivenSource {

    public static final String CONFIG_PORT = "port";
    public static final String CONFIG_BIND = "bind";
    public static final String DEFAULT_BIND = "0.0.0.0";
    private static final String AUTHORIZATION_ID_KEY = "AuthorizationID";
    private static final String EVENTS_KEY = "events";


    private static Logger logger = LoggerFactory.getLogger(PresidioHttpSource.class);

    private volatile Integer port;
    private volatile String host;

    private HTTPSourceHandler handler;

    public volatile Server server;

    private SourceCounter sourceCounter;

    private String authorizationId = "1234";

    public void configure(Context context) {
        try {
            port = context.getInteger(CONFIG_PORT);
            host = context.getString(CONFIG_BIND, DEFAULT_BIND);

            Preconditions.checkState((host != null) && (!host.isEmpty()),
                    "The specified hostname is empty");

            Preconditions.checkNotNull(port, "The specified port is empty");

            String handlerClassName = context.getString(
                    HTTPSourceConfigurationConstants.CONFIG_HANDLER,
                    HTTPSourceConfigurationConstants.DEFAULT_HANDLER).trim();

            Class<? extends HTTPSourceHandler> clazz =
                    (Class<? extends HTTPSourceHandler>)
                            Class.forName(handlerClassName);
            handler = clazz.getDeclaredConstructor().newInstance();
            Map<String, String> subProps =
                    context.getSubProperties(
                            HTTPSourceConfigurationConstants.CONFIG_HANDLER_PREFIX);
            handler.configure(new Context(subProps));
        } catch (ClassCastException e) {
            logger.error("Deserializer is not an instance of HTTPSourceHandler."
                    + "Deserializer must implement HTTPSourceHandler.");
        } catch (Exception e) {
            logger.error("Error configuring HTTPSource!", e);
        }

        if (sourceCounter == null) {
            sourceCounter = new SourceCounter(getName());
        }
    }


    /*
        @Override
        public synchronized void start() {
            logger.info("Starting {}...", getName());

            // authorizationId = readAuthorizationId()
            //TODO: Read authorizationId from config server
            // Options:
            // 1. Using spring (spring client)
            // 2. Using Rest to retrieve value from config server


            try {

                //org.mortbay.jetty.servlet.Context root = new org.mortbay.jetty.servlet.Context(
                  //      server, "/", org.mortbay.jetty.servlet.Context.SESSIONS);



                server = new Server(port);
                ServletContextHandler ctx = new ServletContextHandler(ServletContextHandler.SESSIONS);
                ctx.setContextPath("/");
                server.setHandler(ctx);


                ServletHolder serHol = ctx.addServlet(ServletContainer.class, "/*");
                serHol.setInitOrder(1);
                serHol.setInitParameter("jersey.config.server.provider.packages", "source.presidioHttpSource");
                serHol.setInitParameter("jersey.config.server.tracing.type","ALL");
                serHol.setInitParameter("jersey.config.server.tracing.threshold","TRACE");
                serHol.setInitParameter("com.sun.jersey.config.feature.Trace", "true");
                serHol.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature", "true");

                server.start();

            } catch (Exception e) {
                logger.error("Error while starting Presidio HTTP Source. Exception:", e);
            }

            Preconditions.checkArgument(server.isRunning());

            sourceCounter.start();
            super.start();

            logger.info("Started {}.", getName());
        }*/



    @Override
    public void start(){
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);

        File base = new File(".");
        org.apache.catalina.Context context = tomcat.addContext("", base.getAbsolutePath());

        Tomcat.addServlet(context, "default", new DefaultServlet());
        context.addServletMapping("/*", "default");
        //context.addServletMappingDecoded("/*", "default");

        final FilterDef def = new FilterDef();
        final FilterMap map = new FilterMap();

        def.setFilterName("jerseyFilter");
        def.setFilter(getJerseyFilter());
        context.addFilterDef(def);

        map.setFilterName("jerseyFilter");
        map.addURLPattern("/api/v1.0");
        context.addFilterMap(map);

        try {
            tomcat.start();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
    }

    /*
    @Override
    public void start() {
        Preconditions.checkState(server == null,
                "Running HTTP Server found in source: " + getName()
                        + " before I started one."
                        + "Will not attempt to start.");
        server = new Server();



        // Connector Array
        Connector[] connectors = new Connector[1];


        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setReuseAddress(true);
        connectors[0] = connector;

        connectors[0].setHost(host);
        connectors[0].setPort(port);
        server.setConnectors(connectors);
        try {

            org.mortbay.jetty.servlet.Context root = new org.mortbay.jetty.servlet.Context(
                    server, "/", org.mortbay.jetty.servlet.Context.SESSIONS);

            root.


            HTTPServerConstraintUtil.enforceConstraints(root);
            server.start();
            Preconditions.checkArgument(server.getHandler().equals(root));
        } catch (Exception ex) {
            logger.error("Error while starting HTTPSource. Exception follows.", ex);
            Throwables.propagate(ex);
        }
        Preconditions.checkArgument(server.isRunning());
        sourceCounter.start();
        super.start();
    }*/


    @Override
    public void stop() {
        logger.info("Stopping {}...", getName());

        try {
            server.stop();
            //server.join();
            server.destroy();

            server = null;
        } catch (Exception ex) {
            logger.error("Error while stopping presidio http source. Exception:", ex);
        }
        sourceCounter.stop();

        super.stop();

        logger.info("Presidio Http source {} stopped. Metrics: {}", getName(), sourceCounter);
    }

    public long getBackOffSleepIncrement() {
        return 0;
    }

    public long getMaxBackOffSleepInterval() {
        return 0;
    }


    private static Filter getJerseyFilter() {
        final ResourceConfig config = new ResourceConfig()
                .register(CustomHTTPServlet.class)
                .register(JspMvcFeature.class)
                .property(ServletProperties.FILTER_FORWARD_ON_404, true);
        return new ServletContainer(config);
    }
    /*
        private class FlumeHTTPServlet extends HttpServlet {

            @Override
            public void doPost(HttpServletRequest request, HttpServletResponse response)
                    throws IOException {
                List<Event> events;

                try {
                    events = handler.getEvents(request);

                } catch (HTTPBadRequestException e) {
                    logger.error("Received bad request from client. ", e);
                    response.sendError(ResponseCodes.INTERNAL_SERVER_ERROR,
                            "Bad request from client. "
                                    + e.getMessage());
                    return;
                } catch (Exception e) {
                    logger.error("Deserializer threw unexpected exception. ", e);
                    response.sendError(ResponseCodes.INTERNAL_SERVER_ERROR,
                            "Deserializer threw unexpected exception. "
                                    + e.getMessage());
                    return;
                }

                // Event validation
                String requestAuthId = request.getParameter(AUTHORIZATION_ID_KEY);
                if (!validateEvents(requestAuthId)) {
                    response.sendError(ResponseCodes.UNAUTHORIZED);
                    return;
                }
                if (isBackPressure()) {
                    response.sendError(ResponseCodes.TOO_MANY_REQUESTS);
                    return;
                }

                response.setStatus(ResponseCodes.OK);


                try {
                    getChannelProcessor().processEventBatch(events);
                } catch (ChannelException ex) {
                    logger.error("Error appending event to channel. "
                            + "Channel might be full. Consider increasing the channel "
                            + "capacity or make sure the sinks perform faster.", ex);
                    response.sendError(ResponseCodes.INTERNAL_SERVER_ERROR,
                            "Error appending event to channel. Channel might be full."
                                    + ex.getMessage());
                    return;
                } catch (Exception ex) {
                    logger.error("Unexpected error appending event to channel. ", ex);
                    response.sendError(ResponseCodes.INTERNAL_SERVER_ERROR,
                            "Unexpected error while processing the data. "
                                    + ex.getMessage());
                    return;
                }

                response.setCharacterEncoding(request.getCharacterEncoding());
                response.flushBuffer();
                sourceCounter.incrementAppendBatchAcceptedCount();
                sourceCounter.addToEventAcceptedCount(events.size());
            }
        }
    */
    private Boolean validateEvents(String authorizationId) {
        return this.authorizationId.equals(authorizationId);
        // TODO: check expiration
    }

    private Boolean isBackPressure() {
        //return Math.random() < 0.1;
        return false;
    }

    public static class CustomHTTPServlet {

/*
        @POST
        @Path("/webhook")
        public Response handleNotificationEvent(NotificationEvents events) {


            if (!validateEvents(events.getAuthorizationId())) {
                return Response.status(ResponseCodes.UNAUTHORIZED).build();
            }
            if (isBackPressure()) {
                return Response.status(ResponseCodes.TOO_MANY_REQUESTS).build();
            }
            List<Event> flumeEvents = buildFlumeEventList(events);

            try {
                getChannelProcessor().processEventBatch(flumeEvents);
            } catch (ChannelException ex) {
                logger.error("Error appending event to channel. "
                        + "Channel might be full. Consider increasing the channel "
                        + "capacity or make sure the sinks perform faster.", ex);
                return Response.status(ResponseCodes.INTERNAL_SERVER_ERROR).build();
            } catch (Exception ex) {
                logger.error("Unexpected error appending event to channel. ", ex);
                return Response.status(ResponseCodes.INTERNAL_SERVER_ERROR).build();
            }

            sourceCounter.incrementAppendBatchAcceptedCount();
            sourceCounter.addToEventAcceptedCount(events.getData().getEvents().length);

            return Response.status(ResponseCodes.OK).build();
        }

        @POST
        @Path("/heartbeat")
        @Consumes(MediaType.APPLICATION_JSON)
        public Response handleHeartbeatEvent(HeartbeatEvent hearbeat) {
            if (!validateEvents(hearbeat.getAuthorizationId())) {
                return Response.status(ResponseCodes.UNAUTHORIZED).build();
            }

            return Response.ok().build();
        }*/

        @POST
        @Path("/test")
        public Response handleTest() {
            return Response.ok().build();
        }

        private List<Event> buildFlumeEventList(NotificationEvents events) {
            List<Event> flumeEvents = new ArrayList<Event>(events.getData().getEvents().length);

            for (NotificationEvents.NotificationEvent notificationEvent : events.getData().getEvents()) {
                Event flumeEvent = EventBuilder.withBody(notificationEvent.getEvent(), Charset.defaultCharset());
                flumeEvents.add(flumeEvent);
            }

            return flumeEvents;
        }
    }
}
