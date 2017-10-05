import * as ACTION_TYPES from 'investigate-events/actions/types';

const DEFAULT_DATA = {
  data: {
    metaPanelSize: undefined,
    reconSize: undefined
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
    data: [
      { 'id': 'id1', 'displayName': 'Service Name', 'name': 'CONCENTRATOR' },
      { 'id': 'id2', 'displayName': 'Service Name2', 'name': 'BROKER' }
    ],
    isLoading: false,
    isError: false
  }
};

const getBrokerService = () => DEFAULT_DATA.services.data[1];
const getBrokerServiceId = () => DEFAULT_DATA.services.data[1].id;
const getConcentratorService = () => DEFAULT_DATA.services.data[0];
const getConcentratorServiceId = () => DEFAULT_DATA.services.data[0].id;

export default class DataHelper {
  constructor(redux) {
    this.dispatch = (type, payload) => {
      redux.dispatch({ type, payload });
    };
  }

  initializeData(inputs = DEFAULT_DATA) {
    this.dispatch(ACTION_TYPES.INITIALIZE, inputs);
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

  setQueryParams(params) {
    this.dispatch(ACTION_TYPES.SET_QUERY_PARAMS, params);
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
}

export {
  getBrokerService,
  getBrokerServiceId,
  getConcentratorService,
  getConcentratorServiceId
};
