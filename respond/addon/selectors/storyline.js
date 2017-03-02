import reselect from 'reselect';
import StoryPoint from 'respond/utils/storypoint/storypoint';
import arrayFlattenBy from 'respond/utils/array/flatten-by';
import eventsToNodesAndLinks from 'respond/utils/entity/events-to-nodes-links';

const { createSelector } = reselect;

const storyline = (state) => state.respond.incident.storyline || [];
const defaultNodeRadius = (state) => state.defaultNodeRadius || 0;

// Wraps each storyline member in a util class with a friendlier API.
export const storypoints = createSelector(
  storyline,
  (storyline) => {
    return storyline.map((item) => StoryPoint.create(item));
  }
);

export const storyEvents = createSelector(
  storyline,
  (storyline) => {
    return arrayFlattenBy(storyline, 'indicator.normalizedEvents');
  }
);

export const storyNodesAndLinks = createSelector(
  [ storyEvents, defaultNodeRadius ],
  (events, radius) => {
    return eventsToNodesAndLinks(events, radius);
  }
);