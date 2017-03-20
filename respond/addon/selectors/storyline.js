import reselect from 'reselect';
import Indicator from 'respond/utils/indicator/indicator';
import StoryPoint from 'respond/utils/storypoint/storypoint';
import arrayFlattenBy from 'respond/utils/array/flatten-by';
import arrayFindByList from 'respond/utils/array/find-by-list';
import arrayFromHash from 'respond/utils/array/from-hash';
import eventsToNodesAndLinks from 'respond/utils/entity/events-to-nodes-links';
import { parseNodeId, countNodesByType } from 'respond/utils/entity/node';

const { createSelector } = reselect;

const storyline = (state) => state.respond.incident.storyline || [];

// Flattens a raw storyline object into an array, wraps each of the array items' `indicator` attrs in a utility class.
export const storylineNormalized = createSelector(
  storyline,
  (storyline) => {
    const { relatedIndicators = [] } = storyline;

    // wrap the `indicator` attr of each array member
    relatedIndicators.forEach((item) => {
      item.indicator = Indicator.create(item.indicator);
    });

    // sort by the indicator timestamps (ascending)
    return relatedIndicators.sortBy('indicator.timestamp');
  }
);

// Wraps each normalized storyline member in a util class with a friendlier API.
export const storyPoints = createSelector(
  storylineNormalized,
  (storyline) => {
    return storyline.map((item) => StoryPoint.create(item));
  }
);

// Counts all the storyPoints in the storyline.
export const storyPointCount = createSelector(
  storyPoints,
  (storyPoints) => storyPoints.length
);

export const storySelection = ({ respond: { incident: { selection } } }) => selection;

// Returns array all the currently selected storyPoint ids, if any; otherwise empty array.
export const storyPointSelections = createSelector(
  [ storySelection ],
  ({ type, ids }) => ((type === 'storyPoint') ? ids : [])
);

// Returns array all the currently selected storyEvent ids, if any; otherwise empty array.
export const storyEventSelections = createSelector(
  [ storySelection ],
  ({ type, ids }) => ((type === 'event') ? ids : [])
);

// Collects all the normalized events of a normalized storyline into a single flat array.
export const storyEvents = createSelector(
  storylineNormalized,
  (storyline) => {
    return arrayFlattenBy(storyline, 'indicator.normalizedEvents');
  }
);

// Counts all the normalized events in the storyline.
export const storyEventCount = createSelector(
  storyEvents,
  (storyEvents) => storyEvents.length
);

// Generates the nodes & links from a given array of normalized events.
export const storyNodesAndLinks = createSelector(
  [ storyEvents ],
  (events) => {
    return eventsToNodesAndLinks(events);
  }
);

// Generates a filter for the storyline's nodes & links from the current storyline selection (if any).
export const storyNodesAndLinksFilter = createSelector(
  [ storyNodesAndLinks, storySelection ],
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

// Returns the count of all nodes in `storyNodesAndLinks`, grouped by node type.
// This is the count of all the nodes in the current storyline, irregardless of the current filter.
export const storyNodeCounts = createSelector(
  [ storyNodesAndLinks ],
  ({ nodes = [] }) => {
    return arrayFromHash(
      countNodesByType(nodes)
    );
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