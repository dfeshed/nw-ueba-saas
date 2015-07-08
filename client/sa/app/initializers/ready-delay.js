/**
 * @file Inserts an artificial delay before the app is ready. The amount of the delay
 * is determined by the environment setting ENV.APP.readyDelay (in milliseconds).
 */
import config from '../config/environment';

export function initialize(container, app) {
    // Read the delay amount from the config, if any (default = 0).
    var ms = Math.max(0, parseInt(config.APP.readyDelay, 10) || 0);
    if (ms) {

        // Don't let the app become ready yet.
        app.deferReadiness();

        // After a pause, let the app become ready.
        window.setTimeout(function(){
            app.advanceReadiness();
        }, ms);
    }
}

export default {
  name: 'ready-delay',
  initialize: initialize
};
