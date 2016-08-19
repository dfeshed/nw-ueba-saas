/**
 * @file Initializes the request service.
 * Injects the request service as a global 'reqest' into routes.
 * @public
 */
export function initialize(application) {
  application.inject('route', 'request', 'service:request');
}

export default {
  name: 'request-initializer',
  initialize
};