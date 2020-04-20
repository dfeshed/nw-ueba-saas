import Component from '@ember/component';
import layout from './template';

/**
 * @class Event Details Header component
 * Renders a header for a normalized alert event.
 * @public
 */
export default Component.extend({
  tagName: 'header',
  layout,
  classNames: ['rsa-event-details-header'],

  /**
   * The normalized alert event POJO.
   * @type {object}
   * @public
   */
  model: null
});
