import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import eventsToNodesAndLinks from 'respond/utils/entity/events-to-nodes-links';
import selectionToFilter from 'respond/utils/entity/selection-to-filter';
import arrayFromHash from 'respond/utils/array/from-hash';
import { parseNodeId, countNodesByType } from 'respond/utils/entity/node';
import { connect } from 'ember-redux';
import { storyEvents } from 'respond/selectors/storyline';
import CanThrottleAttr from 'respond/mixins/can-throttle-attr';
import { setHideViz, clearSelection } from 'respond/actions/creators/incidents-creators';

const stateToComputed = (state) => ({
  events: storyEvents(state),
  selection: state.respond.incident.selection
});

const dispatchToActions = (dispatch) => ({
  showAll: () => {
    dispatch(setHideViz(true));
    dispatch(clearSelection());
  }
});

const IncidentEntities = Component.extend(CanThrottleAttr, {
  // no element needed, just the child force layout
  tagName: '',
  fitToSize: null,
  events: null,
  selection: null,

  // Configuration for throttling values from "events" to "eventsThrottled".
  // @see respond/mixins/can-throttle-attr
  throttleFromAttr: 'events',
  throttleToAttr: 'eventsThrottled',
  throttleInterval: 1000,

  // Optional max number of nodes to be rendered (for performance governing).
  nodeLimit: 500,

  // Generates a set of nodes & links from a list of events.
  @computed('eventsThrottled', 'nodeLimit')
  data(events, nodeLimit) {
    return eventsToNodesAndLinks(events || [], { nodeLimit });
  },

  // False only if both of the following conditions are true:
  // (1) the data exceeded node count limit, and
  // (2) the current selection includes an event/alert id that was truncated.
  @computed('data', 'selection')
  selectionCanBeRendered(data, selection) {
    const { hasExceededNodeLimit, indicatorIdsNotRendered, eventIdsNotRendered } = data || {};
    if (hasExceededNodeLimit && selection) {
      const { type, ids = [] } = selection;
      const hash = (type === 'event') ? eventIdsNotRendered : indicatorIdsNotRendered;
      if (ids.any((id) => hash.hasOwnProperty(id))) {
        return false;
      }
    }
    return true;
  },

  // Generates a filter for the nodes & links from the current selection (if any).
  @computed('data', 'selection', 'selectionCanBeRendered')
  filter(data, selection, selectionCanBeRendered) {
    return selectionToFilter(data, selectionCanBeRendered ? selection : null);
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

export default connect(stateToComputed, dispatchToActions)(IncidentEntities);
