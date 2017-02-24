import Ember from 'ember';
import computed, { alias } from 'ember-computed-decorators';
import eventTime from './event-time';

const { Object: EmberObject } = Ember;

/**
 * @class Indicator wrapper
 * A wrapper for RSA Incident Management's Indicator object, with a UI-friendly API for accessing data.
 *
 * The raw indicator JSON that comes from server is, well, not the most intuitive thing you'll ever see.
 * For example, do you want to know the indicator source? Do you think you should look at the `source` property? Fool!
 * It's actually under either `originalHeaders.model_name` or `originalHeaders.device_product` depending on the type.
 *
 * The job of this class is to:
 * (1) encapsulate & flatten some of the JSON complexity using aliases & computed properties;
 * (2) create an array of "normalized" event objects for easy consumption; (want the raw events?  you'll have to look
 * in either `alert.events` or `alert.relationships`, depending on what specifically you want to know about the events)
 * (3) aggregate the enrichments from all the indicator's events into a single hash lookup for easy consumption.
 *
 * @public
 */
export default EmberObject.extend({
  i18n: null,

  id: null,

  /**
   * Time at which this indicator was created.
   * @type {number}
   * @public
   */
  timestamp: null,

  /**
   * Display name for the indicator.
   * @type {string}
   * @public
   */
  @alias('originalHeaders.name')
  name: null,

  /**
   * A list of wrapper objects for the alert events, sorted chronologically (ascending).
   * *
   * Maps the `alert.relationships` array & `alert.events` array to a single array of event wrapper POJOs.
   * Each event wrapper POJO has attrs:
   * `time` (number): the event time if available, otherwise the alert time;
   * `indicatorId` (string): id of the parent alert;
   * `indicatorName` (string): name of the parent alert;
   * `id` (string): manufactured from `indicatorId` and the event's index in its original `alert.relationships` array.
   *
   * Additionally, the event wrapper object may have any of the following (optional) attrs, if they can be parsed from
   * the `alert.relationships` data:
   * `user` (string);
   * `host` (string);
   * `sourceIp` (string);
   * `destinationIp` (string);
   * `hash` (string);
   * `domain` (string);
   * `enrichment` (object).
   *
   * @type {object[]}
   * @public
   */
  @computed('alert.{relationships,events}')
  normalizedEvents(relationships = [], events = []) {
    const {
      id: indicatorId,
      name: indicatorName,
      timestamp: indicatorTime
    } = this.getProperties('id', 'name', 'timestamp');

    return relationships.map((relationship, idx) => {

      // Is there a corresponding item in `indicator.alert.events[]` for this relationship?
      // In some cases, there may not be. For example, the backend does some extra "lookup" queries to match
      // users & ips. These lookup queries may find events that are outside this data set.  However, the results of
      // those findings will be appended to the relationships array, even though the corresponding events will not
      // be appended to the events array. So the relationships array may be longer than the events array.
      const evt = events[idx] || {};
      const time = eventTime(evt) || indicatorTime;

      // Deconstruct the `relationship`, which is an ordered set whose indices imply meaning:
      const [ user, host, domain, sourceIp, destinationIp, file ] = relationship;

      return {
        id: [ indicatorId, idx ].join(':'),
        time,
        indicatorId,
        indicatorName,
        user,
        host,
        sourceIp,
        destinationIp,
        file,
        domain,
        enrichment: evt.enrichment
      };
    }).sortBy('time');
  },

  /**
   * Aggregates the enrichments of this indicator's events into a single hash.
   *
   * Currently, assumes that only the enrichment object of the latest event matters. Why? Because if a stream of events
   * is put thru an enrichment model, every event will be similarly enriched by the model. The enrichment scores will
   * get increasingly refined as more events come thru the model, providing it additional data points with which to
   * apply its correlation test. Thus the scores that are assigned to the last event of the stream will reflect the
   * largest data sample and therefore be the most accurate.  At least, that is the assumption we can make for now
   * given we don't have many data science models yet (according to backend folks I talked to).
   *
   * Note, however, that this assumes that all the events in this indicator all went thru just a single data science
   * model as a single stream. Otherwise, if that's not true, then the events before the last event could have
   * enrichment scores from other data science models. in that case we would be overlooking those scores here. But
   * again we currently don't worry about that because we only have 1 working model.
   *
   * TODO Once we have multiple working models, support aggregating enrichments from multiple events.
   *
   * @type {object[]}
   * @public
   */
  @computed('normalizedEvents.[]')
  enrichments(evts) {
    // Find the last event with enrichments (if any) and return its enrichments.
    // Alas, the last event in the list might not be the last events with enrichments.  So we need to walk the
    // events list backwards (i.e., reverse chronologically).
    const len = this.get('normalizedEvents.length') || 0;
    let i;
    for (i = len - 1; i > -1; i--) {
      const evt = evts[i];
      const { enrichment } = evt;
      if (enrichment) {
        return enrichment;
      }
    }
    return undefined;
  }
});
