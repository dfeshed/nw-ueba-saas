import Ember from 'ember';
import { equal, alias } from 'ember-computed-decorators';

const { Object: EmberObject } = Ember;

/**
 * @class StoryPoint
 * A wrapper for the storyline data objects, with aliases & computed properties for easier data access, particularly
 * for data that will be rendered in DOM or frequently accessed.
 * @public
 */
export default EmberObject.extend({

  /**
   * Information about this storypoint's indicator, such as the alert definition & when it was triggered.
   * @see respond/utils/indicator/indicator.js
   *
   * @type {Ember.Object}
   * @public
   */
  indicator: null,

  /**
   * An incident can have 1 or more groups of indicators. For example, all the indicators which are "catalysts" belong
   * in one group (`'0'`). Currently all the other indicators belong in the only other group (`''`). In the future,
   * there may be more groups defined (e.g., the group might specify how many "degrees of separation" there are
   * between a related indicator & the incident catalysts).
   *
   * @type {string}
   * @public
   */
  group: '',

  /**
   * For a non-catalyst indicator, a list of 1 or more entities from the catalyst that were found in this related indicator.
   * Essentially, this tells us how this indicator is related to another indictator (the catalyst).
   *
   * @type {string[]}
   * @public
   *
   */
  matched: null,

  /**
   * Currently unused. Apparently holds additional info that maps users to ips.
   * TODO Need to learn more about this object and how to display its info in the UI.
   * @type {object}
   * @public
   */
  lookup: null,

  @alias('indicator.id')
  id: null,

  @alias('indicator.timestamp')
  time: null,

  @alias('indicator.name')
  name: null,

  /**
   * Indicates whether this indicator is a "catalyst" alert, i.e. one of the alerts that originally triggered an incident
   * according to the incident's rules.
   *
   * Typically an incident is created by the triggering of 1 or more alerts. However, the analyst may add other alerts
   * to an incident if they are deemed relevant. Those related alerts are not considered "catalysts".
   *
   * To detect a catalyst, we check the storypoint object's `group` attr. It will be `'0'` for catalysts, `''` otherwise.
   * @type {boolean}
   * @public
   */
  @equal('group', '0')
  isCatalyst: null,

  @alias('indicator.normalizedEvents')
  events: null
});