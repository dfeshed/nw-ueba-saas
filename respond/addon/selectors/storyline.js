import reselect from 'reselect';
import Indicator from 'respond/utils/indicator/indicator';
import StoryPoint from 'respond/utils/storypoint/storypoint';
import arrayFlattenBy from 'respond/utils/array/flatten-by';
import arrayFindByList from 'respond/utils/array/find-by-list';
import eventsToNodesAndLinks from 'respond/utils/entity/events-to-nodes-links';

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

