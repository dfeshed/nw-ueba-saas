import reselect from 'reselect';
import StoryPoint from 'respond/utils/storypoint/storypoint';

const { createSelector } = reselect;

const storyline = (respond) => respond.incident.storyline || [];

// Wraps each storyline member in a util class with a friendlier API.
export const storypoints = createSelector(
  storyline,
  (storyline) => {
    return storyline.map((item) => StoryPoint.create(item));
  }
);