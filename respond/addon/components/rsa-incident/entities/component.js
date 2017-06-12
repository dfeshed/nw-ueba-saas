import Component from 'ember-component';
import computed from 'ember-computed-decorators';
import eventsToNodesAndLinks from 'respond/utils/entity/events-to-nodes-links';
import selectionToFilter from 'respond/utils/entity/selection-to-filter';
import arrayFromHash from 'respond/utils/array/from-hash';
import { parseNodeId, countNodesByType } from 'respond/utils/entity/node';
import { throttle } from 'ember-runloop';
import connect from 'ember-redux/components/connect';
import { storyEvents } from 'respond/selectors/storyline';

import Ember from 'ember';
const { observer } = Ember;

const stateToComputed = (state) => ({
  events: storyEvents(state),
  selection: state.respond.incident.selection
});

const IncidentEntities = Component.extend({
  // no element needed, just the child force layout
  tagName: '',
  fitToSize: null,
  events: null,
  selection: null,

  // Same as `events` but is updated by a throttle.
  eventsThrottled: null,

  // Configurable throttle interval (in millisec).
  eventsThrottle: 1000,

  // Kicks off the throttle which will update `eventsThrottled` whenever `events` is reset.
  // We must use an observer here, rather than didReceiveAttrs, because we want to bind `events` to ember-redux, and
  // ember-redux will not fire didReceiveAttrs with each update.
  eventsDidChange: observer('events', function() {
    const ms = this.get('eventsThrottle');
    if (ms && this.get('events.length')) {
      throttle(this, 'syncEventsThrottled', ms, false);
    } else {
      this.syncEventsThrottled();
    }
  }).on('didInsertElement'),

  // Updates `eventsThrottled` to match the latest `events`.
  syncEventsThrottled() {
    if (!this.get('isDestroying') && !this.get('isDestroyed')) {
      this.set('eventsThrottled', this.get('events'));
    }
  },

  // Generates a set of nodes & links from a list of events.
  @computed('eventsThrottled')
  data(events) {
    return eventsToNodesAndLinks(events || []);
  },

  // Generates a filter for the nodes & links from the current selection (if any).
  @computed('data', 'selection')
  filter(data, selection) {
    return selectionToFilter(data, selection);
  },

  // Computes the counts of data nodes, grouped by node type.
  @computed('data.nodes')
  nodeCounts(nodes) {
    return arrayFromHash(
      countNodesByType(nodes)
    );
  },

  // Computes the counts of data nodes that pass the current `filter`, grouped by node type.
  // If there is no current `filter`, counts all of the data nodes.
  @computed('nodeCounts', 'filter')
  filteredNodeCounts(counts, filter) {
    if (!filter) {
      return counts;
    } else {
      const { nodeIds = [] } = filter;
      const nodes = nodeIds.map(parseNodeId);
      return arrayFromHash(
        countNodesByType(nodes)
      );
    }
  }
});

export default connect(stateToComputed)(IncidentEntities);
