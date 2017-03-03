import reselect from 'reselect';
import Indicator from 'respond/utils/indicator/indicator';
import StoryPoint from 'respond/utils/storypoint/storypoint';
import arrayFlattenBy from 'respond/utils/array/flatten-by';
import eventsToNodesAndLinks from 'respond/utils/entity/events-to-nodes-links';

const { createSelector } = reselect;

const storyline = (state) => state.respond.incident.storyline || [];
const defaultNodeRadius = (state) => state.defaultNodeRadius || 0;

// Flattens a raw storyline object into an array, wraps each of the array items' `indicator` attrs in a utility class.
export const normalizedStoryline = createSelector(
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
export const storypoints = createSelector(
  normalizedStoryline,
  (storyline) => {
    return storyline.map((item) => StoryPoint.create(item));
  }
);

// Collects all the normalized events of a normalized storyline into a single flat array.
export const storyEvents = createSelector(
  normalizedStoryline,
  (storyline) => {
    return arrayFlattenBy(storyline, 'indicator.normalizedEvents');
  }
);

// Generates the nodes & links from a given array of normalized events.
export const storyNodesAndLinks = createSelector(
  [ storyEvents, defaultNodeRadius ],
  (events, radius) => {
    return eventsToNodesAndLinks(events, radius);
  }
);