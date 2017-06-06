import EmberObject from 'ember-object';
import { alias } from 'ember-computed-decorators';

/**
 * @class StoryPoint
 * A wrapper object for indicators that are to be placed in the storyline.
 * Such indicators will need a list of events, which must be fetched from a separate API and then stored in this
 * object with the indicator definition.
 * @public
 */
export default EmberObject.extend({

  /**
   * The indicator POJO that this instance wraps.
   * @type {Object}
   * @public
   */
  indicator: null,

  @alias('indicator.id')
  id: null,

  /**
   * The list of events for this indicator.
   * @type {Object[]}
   * @public
   */
  events: null
});