import * as ACTION_TYPES from 'respond/actions/types';
import reduxActions from 'redux-actions';
import fixNormalizedEvents from './util/events';
import { handle } from 'redux-pack';

const initialState = {
  // id of the incident that owns `info` & `storyline`
  id: null,

  // incident storyline information
  storyline: null,

  // either 'streaming', 'error' or 'completed'
  storylineStatus: null,

  // function to stop the current `storyline` stream request, if any
  stopStorylineStream: null,

  // events for the alerts currently in `storyline`
  storylineEvents: null,

  // buffer for storyline events so that they can accumulate without affecting render performance, and then flush
  // to the storylineEvents array once the storylineEventsBufferMax has been reached
  storylineEventsBuffer: [],

  // When the number of events in the buffer exceeds the max, the buffer will be flushed into the storylineEvents array
  storylineEventsBufferMax: 50,

  // status of the current request for storyline events, if any; either 'streaming', 'paused', 'complete' or 'error'
  storylineEventsStatus: null
};

const storyline = reduxActions.handleActions({

  [ACTION_TYPES.INITIALIZE_INCIDENT]: (state, { payload }) => {
    return {
      ...initialState,
      // reset state for a new incident id, even if it matches the old incident id,
      // because we don't want to reuse info, we want to reload it in case it may have changed on server
      id: payload
    };
  },

  [ACTION_TYPES.FETCH_INCIDENT_STORYLINE_STARTED]: (state) => {
    return {
      ...state,
      storyline: [],
      storylineStatus: 'streaming'
    };
  },

  [ACTION_TYPES.FETCH_INCIDENT_STORYLINE_STREAM_INITIALIZED]: (state, { payload }) => {
    return {
      ...state,
      stopStorylineStream: payload
    };
  },

  [ACTION_TYPES.FETCH_INCIDENT_STORYLINE_RETRIEVE_BATCH]: (state, { payload: { data, meta } }) => {
    // Tag each retrieved indicator with its parent incident id.
    // This is useful downstream for mapping indicators back to their parent.
    const storylineId = state.id;
    data = data || [];
    data.forEach((indicator) => {
      indicator.storylineId = storylineId;
    });

    const storyline = state.storyline || [];
    return {
      ...state,
      storyline: [ ...storyline, ...data ],
      storylineStatus: meta.complete ? 'completed' : 'streaming'
    };
  },

  [ACTION_TYPES.FETCH_INCIDENT_STORYLINE_COMPLETED]: (state) => {
    return { ...state, stopStorylineStream: null };
  },

  [ACTION_TYPES.FETCH_INCIDENT_STORYLINE_ERROR]: (state) => {
    return {
      ...state,
      storylineStatus: 'error',
      stopStorylineStream: null
    };
  },

  [ACTION_TYPES.FETCH_INCIDENT_STORYLINE_EVENTS_STREAM_INITIALIZED]: (state) => {
    // Don't reset the array here; that is handled in the INITIALIZE_INCIDENT reducer.
    // This action may be called when adding indicators to the storyline, in which case
    // we don't want to lose all the events we already have in the storyline.
    return { ...state, storylineEvents: state.storylineEvents || [] };
  },

  [ACTION_TYPES.FETCH_INCIDENT_STORYLINE_EVENTS_REQUEST_BATCH]: (state) => {
    return { ...state, storylineEventsStatus: 'streaming' };
  },

  [ACTION_TYPES.FETCH_INCIDENT_STORYLINE_EVENTS_RETRIEVE_BATCH]: (state, { payload: { indicatorId, events } }) => {
    events = events || [];
    events.forEach((evt, index) => {
      // Tag each retrieved event with its parent indicator id.
      // This is useful downstream for mapping events back to their parent.
      evt.indicatorId = indicatorId;

      // Ensure each event has an id.
      // This is useful for selecting individual events in the UI.
      if (!evt.id) {
        evt.id = `${indicatorId}:${index}`;
      }
    });

    // Check for data capture & normalization errors and correct them.
    fixNormalizedEvents(events);

    let storylineEvents = state.storylineEvents || [];
    let storylineEventsBuffer = [...state.storylineEventsBuffer, { indicatorId, events }];

    // If we have no events yet, flush the buffer, otherwise
    // flush the buffer whenever we have received more than the buffer max
    if (!storylineEvents.length || storylineEventsBuffer.length > state.storylineEventsBufferMax) {
      storylineEvents = [...storylineEvents, ...storylineEventsBuffer];
      storylineEventsBuffer = [];
    }

    return {
      ...state,
      storylineEvents,
      storylineEventsBuffer,
      storylineEventsStatus: 'paused'
    };
  },

  [ACTION_TYPES.FETCH_INCIDENT_STORYLINE_EVENTS_COMPLETED]: (state) => {
    // When we're done fetching storyline events, make sure we flush the buffer into storylineEvents
    return {
      ...state,
      storylineEvents: [...state.storylineEvents, ...state.storylineEventsBuffer],
      storylineEventsBuffer: [],
      storylineEventsStatus: 'completed'
    };
  },

  [ACTION_TYPES.FETCH_INCIDENT_STORYLINE_EVENTS_ERROR]: (state) => {
    return { ...state, storylineEventsStatus: 'error' };
  },

  [ACTION_TYPES.ADD_RELATED_INDICATORS]: (state, action) => (
    handle(state, action, {
      start: (s) => s,
      success: (s) => {
        const { payload: { searchResults, response: { data: addedIndicatorIds, request: { data: { entity: { id } } } } } } = action;
        const updatedIndicators = [];

        // Update any indicators in searchResults that match the indicators in payload
        searchResults.forEach((indicator) => {
          if (addedIndicatorIds.includes(indicator.id)) {
            // For the indicator(s) added to the incident, ensure they have the updated properties
            const changedIndicator = {
              ...(indicator.asMutable({ deep: true })),
              partOfIncident: true,
              incidentId: id
            };
            // Track all updated indicators so they can be added to the storyline
            updatedIndicators.push(changedIndicator);
          }
        });

        return {
          ...s,
          storyline: [...s.storyline, ...updatedIndicators]
        };
      }
    })
  )

}, initialState);

export default storyline;