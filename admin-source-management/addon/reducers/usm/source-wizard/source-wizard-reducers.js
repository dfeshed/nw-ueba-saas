import Immutable from 'seamless-immutable';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import _ from 'lodash';
import edrSourceInitialState from './edrSource/edr-initialState';
import edrSourceReducers from './edrSource/edr-reducerFns';
import windowsLogSourceReducers from './windowsLogSource/windowsLog-reducerFns';
import windowsLogSourceInitialState from './windowsLogSource/windowsLog-initialState';
import * as ACTION_TYPES from 'admin-source-management/actions/types';

const INITIAL_STATES = {
  edrSource: edrSourceInitialState,
  windowsLogSource: windowsLogSourceInitialState,
  common: {
    //  the source object to be created/updated/saved
    source: {
      // common source props
      id: null,
      sourceType: 'edrSource', // need a default for initialization
      name: '',
      description: '',
      dirty: true,
      lastPublishedCopy: null,
      lastPublishedOn: 0,
      defaultSource: false,
      createdOn: 0
      // source type specific props will be merged in each time we run:
      // - NEW_SOURCE, FETCH_SOURCE (edit), and UPDATE_SOURCE_TYPE
    },
    sourceOrig: {},
    sourceFetchStatus: null, // wait, complete, error (separate one for FETCH)
    sourceStatus: null, // wait, complete, error (for SAVE & SAVE_PUBLISH)
    steps: [
    ],

    // identify-source-step - the source sourceType objects to fill the select/dropdown
    sourceTypes: [
      { id: 'edrSource', sourceType: 'edrSource', name: 'EndpointScan', label: 'adminUsm.sourceWizard.edrSourceType' },
      { id: 'windowsLogSource', sourceType: 'windowsLogSource', name: 'EndpointWL', label: 'adminUsm.sourceWizard.windowsLogSourceType' }
    ],

    // define-source-step - available settings to render the left col
    // source type specific available settings will be merged in each time we run:
    // - NEW_SOURCE, FETCH_SOURCE (edit), and UPDATE_SOURCE_TYPE
    availableSettings: [],
    // define-source-step - selected settings to render the right col
    selectedSettings: [],

    // keeps track of the form fields visited by the user
    visited: [],

    // the summary list of policies objects
    sourceList: [],
    sourceListStatus: null, // wait, complete, error

    // ===================================================
    // edrSource specific state to be fetched
    // ===================================================
    // list of endpoint servers from the orchestration service to populate the hostname drop down
    listOfEndpointServers: [],

    // ===================================================
    // windowsLogSource specific state to be fetched
    // ===================================================
    listOfLogServers: []
  }
};

// run for NEW_SOURCE, FETCH_SOURCE (edit), and UPDATE_SOURCE_TYPE
export const buildInitialState = (state, sourceType, isUpdateSourceType = false) => {
  // reset everything from common initialState & type specific initialState
  let mergedInitialState = state.set('source', {}).merge(
    [{ ...INITIAL_STATES.common }, { ...INITIAL_STATES[sourceType] }],
    { deep: true }
  );
  // keep things we don't want to refetch or overwrite for UPDATE_SOURCE_TYPE
  if (isUpdateSourceType) {
    mergedInitialState = mergedInitialState.merge({
      sourceList: [...state.sourceList],
      // preserve the previously entered name and description when sourceType is toggled.
      visited: [...state.visited],
      source: {
        name: state.source.name,
        description: state.source.description
      }
    }, { deep: true }); // deep merge so we don't reset everything
  }
  return mergedInitialState;
};


export default reduxActions.handleActions({

  [ACTION_TYPES.NEW_SOURCE]: (state /* , action */) => {
    // reset everything from common initialState & type specific initialState
    const mergedInitialState = buildInitialState(state, state.source.sourceType);
    const newState = state.merge({
      ...mergedInitialState,
      sourceOrig: mergedInitialState.source,
      sourceFetchStatus: 'complete',
      sourceStatus: 'complete'
    });
    return newState;
  },

  [ACTION_TYPES.FETCH_SOURCE]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        // reset everything on load start (same as NEW_SOURCE) so things are in sync in case of a load error
        const mergedInitialState = buildInitialState(state, state.source.sourceType);
        const newState = state.merge({
          ...mergedInitialState,
          sourceOrig: mergedInitialState.source,
          sourceFetchStatus: 'wait'
        });
        return newState;
      },
      failure: (state) => {
        return state.set('sourceFetchStatus', 'error');
      },
      success: (state) => {
        const fetchedSource = action.payload.data;
        // reset everything from common initialState & type specific initialState
        const mergedInitialState = buildInitialState(state, fetchedSource.sourceType);

        return mergedInitialState.merge({
          source: fetchedSource,
          sourceOrig: fetchedSource,
          sourceFetchStatus: 'complete'
        });
      }
    })
  ),

  [ACTION_TYPES.FETCH_SOURCE_LIST]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        return state.merge({
          sourceList: [],
          sourceListStatus: 'wait'
        });
      },
      failure: (state) => {
        return state.set('sourceListStatus', 'error');
      },
      success: (state) => {
        return state.merge({
          sourceList: action.payload.data,
          sourceListStatus: 'complete'
        });
      }
    })
  ),

  [ACTION_TYPES.UPDATE_SOURCE_STEP]: (state, action) => {
    const { field, value } = action.payload;
    const fields = field.split('.');
    return state.setIn(fields, value);
  },

  [ACTION_TYPES.DISCARD_SOURCE_CHANGES]: (state) => {
    return state.set('source', state.sourceOrig);
  },

  // define-source-step - add an available setting (left col) as a selected setting (right col)
  [ACTION_TYPES.ADD_TO_SELECTED_SETTINGS]: (state, { payload }) => {
    const id = payload;
    const { selectedSettings, availableSettings } = state;

    const sourceValues = {};
    const newSelectedSettings = availableSettings.find((d) => d.id === id);
    const newAvailableSettings = availableSettings.map((el) => {
      if (el.id === id) {
        // add the added setting's defaults to the source
        const elDefaults = el.defaults || [];
        for (let i = 0; i < elDefaults.length; i++) {
          sourceValues[elDefaults[i].field] = elDefaults[i].value;
        }
        return {
          ...el,
          isEnabled: false
        };
      }
      // if the scan type is "ENABLED" in state, nothing should be greyed out
      // in availableSettings
      if (state.source.scanType === 'ENABLED') {
        return {
          ...el,
          isGreyedOut: false
        };
      }
      return el;
    });
    return state.merge({
      source: {
        ...sourceValues
      },
      availableSettings: newAvailableSettings,
      selectedSettings: _.uniqBy([...selectedSettings, newSelectedSettings], 'id')
    }, { deep: true }); // deep merge so we don't reset everything
  },

  // edrSource actions
  [ACTION_TYPES.FETCH_ENDPOINT_SERVERS]: edrSourceReducers.fetchEndpointServers,

  [ACTION_TYPES.EDR_DEFAULT_SOURCE]: edrSourceReducers.edrDefaultSource,

  // windowsLogSource actions
  [ACTION_TYPES.FETCH_LOG_SERVERS]: windowsLogSourceReducers.fetchLogServers,

  // identify-source-step
  [ACTION_TYPES.UPDATE_SOURCE_TYPE]: (state, action) => {
    const sourceType = action.payload;
    // reset everything from common initialState & type specific initialState
    const mergedInitialState = buildInitialState(state, sourceType, true);
    // update the source type
    return mergedInitialState.setIn('source.sourceType'.split('.'), sourceType);
  },

  [ACTION_TYPES.UPDATE_SOURCE_PROPERTY]: (state, action) => {
    let newState = state;
    const fieldValuePairs = action.payload;
    for (let i = 0; i < fieldValuePairs.length; i++) {
      const { field, value } = fieldValuePairs[i];
      const fields = field.split('.');
      // Edit the value in the source, and keep track of the field as having been visited
      // Visited fields will show error/validation messages
      newState = newState.setIn(fields, value).set('visited', _.uniq([...state.visited, field]));
    }
    return newState;
  },

  [ACTION_TYPES.SAVE_SOURCE]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        return state.set('sourceStatus', 'wait');
      },
      failure: (state) => {
        return state.set('sourceStatus', 'error');
      },
      success: (state) => {
        return state.merge({
          source: action.payload.data,
          sourceOrig: action.payload.data,
          sourceStatus: 'complete'
        });
      }
    })
  ),

  [ACTION_TYPES.SAVE_PUBLISH_SOURCE]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        return state.set('sourceStatus', 'wait');
      },
      failure: (state) => {
        return state.set('sourceStatus', 'error');
      },
      success: (state) => {
        return state.merge({
          source: action.payload.data,
          sourceOrig: action.payload.data,
          sourceStatus: 'complete'
        });
      }
    })
  )

}, Immutable.from(INITIAL_STATES.common));
