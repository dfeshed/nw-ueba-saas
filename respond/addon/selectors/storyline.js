import reselect from 'reselect';
import arrayFlattenBy from 'respond/utils/array/flatten-by';
import arrayFilterByList from 'respond/utils/array/filter-by-list';
import StoryPoint from 'respond/utils/storypoint/storypoint';
import { get, set, setProperties } from '@ember/object';
import { lookup } from 'ember-dependency-lookup';
import { lookupCoreDevice } from 'respond/utils/storypoint/event-analysis';

const { createSelector } = reselect;

const reconState = (state) => state.respond.recon;
const incidentState = (state) => state.respond.incident;
const storylineState = (state) => state.respond.storyline;

/**
 * Retrieves the respond.incident.info object from state, or an empty object.
 * @type {Object}
 * @private
 */
export const incidentInfo = createSelector(
  incidentState,
  (incidentState) => incidentState.info || {}
);

export const storyPointEventSelections = createSelector(
  incidentState,
  (incidentState) => {
    const { selection: { type, ids } } = incidentState;
    return {
      areGroups: type === 'storyPoint',
      ids: (type === 'storyPoint' || type === 'event') ? ids : []
    };
  }
);

export const getStoryline = createSelector(
  storylineState,
  (storylineState) => storylineState.storyline
);

export const getStorylineEvents = createSelector(
  storylineState,
  (storylineState) => storylineState.storylineEvents
);

export const getStorylineStatus = createSelector(
  storylineState,
  (storylineState) => storylineState.storylineStatus
);

/**
 * Retrieves the storyline array (if any) from state, or an empty array.
 * @returns {Object[]}
 * @private
 */
export const incidentIndicators = createSelector(
  getStoryline,
  (storyline) => storyline || []
);

/**
 * Retrieves the storylineEvents array (if any) from state, or an empty array.
 * @type {{ indicatorId: String, events: Object[] }[]}
 * @private
 */
export const storylineEvents = createSelector(
  getStorylineEvents,
  (storylineEvents) => storylineEvents || []
);

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
  (incidentIndicators) => incidentIndicators.map((indicator) => StoryPoint.create({ indicator }))
);

const hasReconAccess = () => {
  const accessControl = lookup('service:accessControl');
  return get(accessControl, 'hasReconAccess');
};

/**
 * The same array as `storyPoints`, but populates each member's `events` property
 * with the data currently in the `storylineEvents` state.
 * @type {Object[]}
 * @private
 */
export const storyPointsWithEvents = createSelector(
  [ storyPoints, storylineEvents, storyPointEventSelections, reconState ],
  (storyPoints, storylineEvents, selections, reconState) => {
    (storyPoints || []).forEach((storyPoint) => {

      // If the storyPoint doesn't have events yet, fetch them from storylineEvents state.
      if (!storyPoint.get('events')) {
        const payload = (storylineEvents || []).findBy('indicatorId', storyPoint.get('indicator.id'));
        if (payload) {
          set(storyPoint, 'events', payload.events);
        }
      }

      // If the storyPoint has its events now, close it if it doesn't have any child items.
      // For example, if the events have no enrichments and the child items are supposed to display enrichments,
      // then there are no child items to display, so we should mark it closed. Otherwise, if we leave it opened,
      // the UI will render it as open but not render any child items, which would be an awkward state.
      const storyPointEvents = storyPoint.get('events');
      if (storyPointEvents) {
        if (!storyPoint.get('items.length')) {
          set(storyPoint, 'isOpen', false);
        } else {
          const eventsWithEventAnalysis = storyPointEvents.filter((item) => {
            const eventId = get(item, 'event_source_id');
            const eventSource = get(item, 'event_source');
            return eventId && hasReconAccess() && lookupCoreDevice(reconState.serviceData, eventSource);
          });
          if (eventsWithEventAnalysis.toArray().length > 0) {
            set(storyPoint, 'supportsRecon', true);
          }
        }

        const selectedIncident = selections && selections.ids && storyPointEvents && storyPointEvents.filter((e) => {
          return e && e.id && selections.ids.includes(e.id);
        })[0];

        if (selectedIncident) {
          setProperties(storyPoint, {
            showEnrichmentsAsItems: false,
            isOpen: true
          });
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
 * Computes the storyline's total event count by comparing two results:
 * (1) the incident info's `eventCount`, which was generated by normalization scripts; and
 * (2) the sum of each `alert.numEvents` for every event in the current `storyline` state.
 *
 * In general, these 2 quantities should be equal. However, if the user manually adds an alert ("related indicator")
 * to the storyline, then quantity (2) will increase while quantity (1) will be out-of-date.
 *
 * Note: This selector is different from the `storyEventCount` selector above.  That selector returns the count of
 * events that have been streamed into the UI so far, which is variable depending on the stream state. This selector
 * returns the expected total count, which is independent of the stream state. Indeed, you don't even have to load
 * any events in order to compute this selector.
 *
 * @returns {Number}
 * @private
 */
export const storyEventCountExpected = createSelector(
  [ incidentInfo, incidentIndicators ],
  (incidentInfo, incidentIndicators) => {
    const originalCount = incidentInfo.eventCount || 0;
    const computedCount = incidentIndicators.reduce(function(total, indicator) {
      return total + (indicator.alert.numEvents || 0);
    }, 0);
    return Math.max(originalCount, computedCount);
  }
);

/**
 * Returns the `respond.incident.selection` state.
 * @private
 */
export const incidentSelection = createSelector(
  incidentState,
  (incidentState) => incidentState.selection
);

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

/**
 * Computes the total count of events currently selected.
 *
 * Note: This selector is designed to be independent of how many event records have been streamed into the UI so far.
 * In fact, you don't even have to load any events to compute this selector.  It relies solely on the `alert.numEvents`
 * of each indicator, not the current count of events streamed so far.
 *
 * Checks the current selection, which may be either (a) indicators, (b) events or (c) empty.
 * If (a): returns the sum of each selected indicator's `alert.numEvents`.
 * If (b): returns the number of selected event ids.
 * If (c): calls the storyEventCountExpected selector above to compute the total event count for the incident.
 *
 * @returns {Number}
 * @private
 */
export const selectedStoryEventCountExpected = createSelector(
  [ storyEventCountExpected, incidentIndicators, incidentSelection ],
  (storyEventCountExpected, incidentIndicators, { type, ids }) => {
    if (!ids || !ids.length) {

      // Nothing selected, so return the unfiltered expected count of events.
      return storyEventCountExpected;
    } else if (type === 'storyPoint') {

      // Indicator(s) are selected, so sum up the events inside those selected indicators.
      const selectedIndicators = incidentIndicators.filter((indicator) => {
        return ids.includes(indicator.id);
      });
      return selectedIndicators.reduce(function(total, indicator) {
        return total + (indicator.alert.numEvents || 0);
      }, 0);

    } else if (type === 'event') {
      // Event(s) are selected, so return the count of selected events.
      return ids.length;
    }
  }
);
