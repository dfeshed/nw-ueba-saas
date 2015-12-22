import Ember from 'ember';
import config from 'style-guide/config/environment';

/**
 * Returns handle to the app's root DOM element, wrapped in Ember.$.
 * @public
 */
export default function domRoot() {
  return Ember.$(config.APP.rootElement || 'body');
}
