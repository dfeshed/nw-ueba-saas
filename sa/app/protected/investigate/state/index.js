import Ember from 'ember';
import Services from './services';
import EventColumns from './event-columns';
import Tree from 'sa/utils/tree/tree';
import Meta from './meta';

const {
  computed,
  Object: EmberObject
} = Ember;

export default EmberObject.extend({

  // List of available Core services. User can choose one to query.
  services: Services.create(),

  // Tree of queries executed (if any) during this user's session. Used for tracking an investigation's path.
  queryTree: Tree.create(),

  // Pointer to the currently visible query's node in the `queryTree`.
  queryNode: undefined,

  // Pointer to the previously shown query node in the `queryTree`. Used for transitioning from node to node.
  lastQueryNode: undefined,

  // Columns to display for events data table.
  eventColumns: [].concat(EventColumns),

  /**
   * State object for the event timeline of the current query.
   * @see investigate/state/event-timeline
   * @type {object}
   * @private
   */
  _currentEventTimeline: computed.alias('queryNode.value.results.eventTimeline'),

  /**
   * State object for the event timeline of the previous query.
   * If given, this time series will be shown as "ghosted" behind the current time series.
   * @see investigate/state/event-timeline
   * @type {object}
   * @private
   */
  _lastEventTimeline: computed.alias('lastQueryNode.value.results.eventTimeline'),

  /**
   * The chart data structure, derived from `_currentEventTimeline` & `_lastEventTimeline`, in a structure that can be
   * understood by the `rsa-chart` component (i.e., an array of arrays).
   * The chart data structure will hold 2 series. If current series is still being fetched, the last series is
   * rendered twice, until the current is ready, at which point one it will replace one of the last series'
   * renderings. This is done in order to render a nice transition in which 1 series (the last series) appears to split
   * into 2 series (the last series + a new current series).
   * @see component-lib/components/rsa-chart
   * @type { [[]] }
   * @public
   */
  resolvedEventTimelineData: computed('_currentEventTimeline.status', '_currentEventTimeline.data', '_lastEventTimeline.data',
    function() {
      const cStatus = this.get('_currentEventTimeline.status');
      const cData = this.get('_currentEventTimeline.data');
      const lData = this.get('_lastEventTimeline.data');
      const waiting = cStatus === 'wait';
      return [
        (waiting ? lData : cData) || [],
        lData || []
      ];
    }
  ),

  // Meta data for the `events`.
  meta: Meta.create()
}).create();
