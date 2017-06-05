import reselect from 'reselect';
import arrayFlattenBy from 'respond/utils/array/flatten-by';
import arrayFindByList from 'respond/utils/array/find-by-list';
import arrayFilterByList from 'respond/utils/array/filter-by-list';
import arrayFromHash from 'respond/utils/array/from-hash';
import eventsToNodesAndLinks from 'respond/utils/entity/events-to-nodes-links';
import { parseNodeId, countNodesByType } from 'respond/utils/entity/node';
import { isEmpty } from 'ember-utils';

const { createSelector } = reselect;

/**
 * Returns the same object as the `respond.incident.storyline` state, but adds the following logic:
 * returns an empty array if the `storyline` is empty, null or undefined;
 * if storyline is not empty, ensures every alert has a 'storylineId' property,
 * and that every alert event has `id` & `indicatorId` properties.
 * These properties are useful in the UI when trying to correlated events & alerts back to their parent storyline.
 * @returns {Object[]}
 * @private
 */
const incidentIndicators = ({ respond: { incident: { id, storyline } } }) => {
  if (storyline) {
    storyline.forEach((indicator) => {

      // If we've already processed this indicator, don't repeat.
      if (!isEmpty(indicator.storylineId)) {
        return;
      }

      // Mark this indicator as processed, so we don't do this twice.
      indicator.storylineId = id;

      const {
        id: indicatorId,
        alert: {
          events = []
        } = {}
      } = indicator;

      events.forEach((event, index) => {
        event.indicatorId = indicatorId;
        if (isEmpty(event.id)) {
          event.id = `${indicatorId}:${index}`;
        }
      });
    });
  }
  return storyline || [];
};

/**
 * Returns the `incidentIndicators` list, sorted by indicator timestamp (ascending).
 * This is the list to be used for displaying the incident on a storyline UI.
 * @returns {Object[]}
 * @public
 */
export const storyPoints = createSelector(
  incidentIndicators,
  (incidentIndicators) => {
    return incidentIndicators.sortBy('timestamp');
  }
);

/**
 * Counts all the `storyPoints` in the storyline.
 * @returns {Number}
 * @public
 */
export const storyPointCount = createSelector(
  storyPoints,
  (storyPoints) => storyPoints.length
);

/**
 * Collects all the normalized events from the incident indicators into a single flat array, sorted chronologically.
 * This is the list to be used for displaying the incident's events in a flat table UI.
 * @returns {Object[]}
 * @public
 */
export const storyEvents = createSelector(
  incidentIndicators,
  (incidentIndicators) => {
    return arrayFlattenBy(incidentIndicators, 'alert.events')
      .sortBy('timestamp');
  }
);

/**
 * Counts all the normalized events in the storyline.
 * @returns {Number}
 * @public
 */
export const storyEventCount = createSelector(
  storyEvents,
  (storyEvents) => storyEvents.length
);

/**
 * Generates the nodes & links from a given array of normalized events.
 * @returns {{ nodes: Object[], links: Object[] }
 * @public
 */
export const storyNodesAndLinks = createSelector(
  [ storyEvents ],
  (events) => {
    return eventsToNodesAndLinks(events);
  }
);

/**
 * Returns the count of all nodes in `storyNodesAndLinks`, grouped by node type.
 * This is the count of all the nodes in the current storyline, irregardless of the current filter.
 * @returns {Number}
 * @public
 */
export const storyNodeCounts = createSelector(
  [ storyNodesAndLinks ],
  ({ nodes = [] }) => {
    return arrayFromHash(
      countNodesByType(nodes)
    );
  }
);

/**
 * Returns the `respond.incident.selection` state.
 * @private
 */
const incidentSelection = ({ respond: { incident: { selection } } }) => selection;

/**
 * If the current selections are storyPoints, returns selected storypoint ids; otherwise empty array.
 * These are the selections for the storyPoints UI.
 * @type {String[]}
 * @public
 */
export const storyPointSelections = createSelector(
  [ incidentSelection ],
  ({ type, ids }) => ((type === 'storyPoint') ? ids : [])
);

/**
 * If the current selections are events, returns selected event ids; otherwise empty array.
 * These are the selections for the storyEvents UI.
 * @type {String[]}
 * @public
 */
export const storyEventSelections = createSelector(
  [ incidentSelection ],
  ({ type, ids }) => ((type === 'event') ? ids : [])
);

// Generates a filter for the storyline's nodes & links from the current storyline selection (if any).
export const storyNodesAndLinksFilter = createSelector(
  [ storyNodesAndLinks, incidentSelection ],
  ({ nodes = [], links = [] }, { type, ids }) => {

    const filterIdsByEvents = (arr, field, values) => {
      return arr
        .filter((item) => !!arrayFindByList(item.events, field, values))
        .map((item) => item.id);
    };

    if (ids && ids.length) {
      switch (type) {
        case 'storyPoint':
          return {
            nodeIds: filterIdsByEvents(nodes, 'indicatorId', ids),
            linkIds: filterIdsByEvents(links, 'indicatorId', ids)
          };
        case 'event':
          return {
            nodeIds: filterIdsByEvents(nodes, 'id', ids),
            linkIds: filterIdsByEvents(links, 'id', ids)
          };
      }
    }
    return null;
  }
);

// Returns either the count of all the nodes in `storyNodesAndLinksFilter`, or
// if storyNodesAndLinksFilter is null returns the count of all nodes in `storyNodesAndLinks`.
// This is the count of all the nodes in the storyline that pass the current filter, if any;
// otherwise if there is no current filter, then it is the count of all the nodes in the storyline at all.
export const storyNodeFilterCounts = createSelector(
  [ storyNodeCounts, storyNodesAndLinksFilter ],
  (storyNodeCounts, filter) => {
    if (!filter) {
      return storyNodeCounts;
    } else {
      const { nodeIds = [] } = filter;
      const nodes = nodeIds.map(parseNodeId);
      return arrayFromHash(
        countNodesByType(nodes)
      );
    }
  }
);

// Returns either the list of all events in the storyline, or the subset of those events that match the
// currently selected storyPoints/events, if there is any selection.
// This is the list of events to be displayed in the datasheet UI.
export const storyDatasheet = createSelector(
  [ storyEvents, incidentSelection ],
  (storyEvents, { type, ids }) => {
    if (!ids || !ids.length) {

      // Nothing selected, so return all the events across all alerts we have, unfiltered.
      return storyEvents;
    } else if (type === 'storyPoint') {

      // Indicator(s) are selected, so filter all events list by the selected indicators.
      return arrayFilterByList(storyEvents, 'indicatorId', ids);
    } else if (type === 'event') {

      // Event(s) are selected, so filter all events list by the selected event ids.
      return arrayFilterByList(storyEvents, 'id', ids);
    }
  }
);