/**
 * @file Inserts an artificial delay before the app is ready. The amount of the delay
 * is determined by the environment setting ENV.APP.readyDelay (in milliseconds).
 * @public
 */

export function initialize(app) {
  let config = app.resolveRegistration('config:environment');
  let delay = 0;

  if (config) {
    delay = config.APP.readyDelay || 0;
  }

  if (delay) {
    // Read the delay amount from the config, if any (default = 0).
    let ms = Math.max(0, parseInt(delay, 10) || 0);
    if (ms) {

      // Don't let the app become ready yet.
      app.deferReadiness();

      // After a pause, let the app become ready.
      window.setTimeout(function() {
        app.advanceReadiness();
      }, ms);
    }
  }
}

export default {
  name: 'ready-delay',
  initialize
};
