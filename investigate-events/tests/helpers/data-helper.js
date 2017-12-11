import * as ACTION_TYPES from 'investigate-events/actions/types';

const DEFAULT_DATA = {
  data: {
    metaPanelSize: undefined,
    reconSize: undefined,
    columnGroup: null
  },
  dictionaries: {
    aliases: undefined,
    aliasesCache: {},
    language: undefined,
    languageCache: {},
    languageError: false,
    aliasesError: false
  },
  queryNode: {
    serviceId: 'id1'
  },
  services: {
    serviceData: [
      { 'id': 'id1', 'displayName': 'Service Name', 'name': 'CONCENTRATOR' },
      { 'id': 'id2', 'displayName': 'Service Name2', 'name': 'BROKER' }
    ],
    isServicesLoading: false,
    isServicesRetrieveError: false,
    summaryData: undefined,
    isSummaryRetrieveError: false,
    summaryErrorMessage: undefined
  }
};

const getBrokerService = () => DEFAULT_DATA.services.serviceData[1];
const getBrokerServiceId = () => DEFAULT_DATA.services.serviceData[1].id;
const getConcentratorService = () => DEFAULT_DATA.services.serviceData[0];
const getConcentratorServiceId = () => DEFAULT_DATA.services.serviceData[0].id;

export default class DataHelper {
  constructor(redux) {
    this.dispatch = (type, payload) => {
      redux.dispatch({ type, payload });
    };
  }

  initializeData(inputs = DEFAULT_DATA) {
    this.dispatch(ACTION_TYPES.INITIALIZE_TESTS, inputs);
    return this;
  }

  setAliases(aliases) {
    this.dispatch(ACTION_TYPES.SET_ALIASES, aliases);
    return this;
  }

  setLanguage(language) {
    this.dispatch(ACTION_TYPES.SET_LANGUAGE, language);
    return this;
  }

  setQueryParamsForTests(params) {
    this.dispatch(ACTION_TYPES.SET_QUERY_PARAMS_FOR_TESTS, params);
    return this;
  }

  setServiceId(id = 'id1') {
    this.dispatch(ACTION_TYPES.SERVICE_SELECTED, id);
    return this;
  }

  setMetaPanelSize(size) {
    this.dispatch(ACTION_TYPES.SET_META_PANEL_SIZE, size);
    return this;
  }

  setPreferences(preferences) {
    this.dispatch(ACTION_TYPES.SET_PREFERENCES, preferences);
    return this;
  }

  setColumnGroup(columnGroupId) {
    this.dispatch(ACTION_TYPES.SET_SELECTED_COLUMN_GROUP, columnGroupId);
    return this;
  }
}

export {
  getBrokerService,
  getBrokerServiceId,
  getConcentratorService,
  getConcentratorServiceId
};
