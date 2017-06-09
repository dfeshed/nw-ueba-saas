import reselect from 'reselect';
import arrayFlattenBy from 'respond/utils/array/flatten-by';
import arrayFilterByList from 'respond/utils/array/filter-by-list';
import StoryPoint from 'respond/utils/storypoint/storypoint';
const { createSelector } = reselect;

/**
 * Retrieves the storyline array (if any) from state, or an empty array.
 * @returns {Object[]}
 * @private
 */
const incidentIndicators = ({ respond: { incident: { storyline } } }) => storyline || [];

/**
 * Retrieves the storylineEvents array (if any) from state, or an empty array.
 * @type {{ indicatorId: String, events: Object[] }[]}
 * @private
 */
const storylineEvents = ({ respond: { incident: { storylineEvents } } }) => storylineEvents || [];

/**
 * Counts all the indicators in the storyline.
 * @returns {Number}
 * @public
 */
export const storyPointCount = createSelector(
  incidentIndicators,
  (indicators) => indicators.length
);

/**
 * Wraps the POJOs of the `incidentIndicators` list in StoryPoint Ember classes.
 * @returns {Object[]}
 * @private
 */
export const storyPoints = createSelector(
  incidentIndicators,
  (incidentIndicators) => {
    return incidentIndicators.map((indicator) => StoryPoint.create({ indicator }));
  }
);

/**
 * The same array as `storyPoints`, but populates each member's `events` property
 * with the data currently in the `storylineEvents` state.
 * @type {Object[]}
 * @private
 */
export const storyPointsWithEvents = createSelector(
  [ storyPoints, storylineEvents ],
  (storyPoints, storylineEvents) => {
    (storyPoints || []).forEach((storyPoint) => {
      if (!storyPoint.get('events')) {
        const payload = (storylineEvents || []).findBy('indicatorId', storyPoint.get('indicator.id'));
        if (payload) {
          storyPoint.set('events', payload.events);
        }
      }
    });
    return storyPoints;
  }
);

/**
 * Sorts the `storyPointsWithEvents` list in ascending chronological order.
 * This is the list to be used for displaying the incident on a storyline UI.
 * @returns {Object[]}
 * @public
 */
export const storyPointsWithEventsSorted = createSelector(
  storyPointsWithEvents,
  (storyPointsWithEvents) => {
    return storyPointsWithEvents.sortBy('indicator.timestamp');
  }
);

/**
 * Collects all the normalized events from the incident indicators into a single flat array.
 * This is the list to be used for displaying the incident's events in a flat table UI.
 * @returns {Object[]}
 * @public
 */
export const storyEvents = createSelector(
  storylineEvents,
  (storylineEvents) => {
    return arrayFlattenBy(storylineEvents, 'events');
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