/**
 * @file Initializes the websocket service.
 * Injects the websocket service as a global 'websocket' into routes & controllers.
 * @public
 */
export function initialize(application) {
  application.inject('route', 'websocket', 'service:websocket');
  application.inject('controller', 'websocket', 'service:websocket');
}

export default {
  name: 'websocket-service',
  initialize
};
