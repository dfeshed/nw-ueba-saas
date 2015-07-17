/**
 * @file Initializes the websocket service.
 * Injects the websocket service as a global "websocket" into routes & controllers.
 */
export function initialize(container, application) {
    application.inject("route", "websocket", "service:websocket");
    application.inject("controller", "websocket", "service:websocket");
}

export default {
  name: "websocket-service",
  initialize: initialize
};
