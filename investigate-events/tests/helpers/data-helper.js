// import run from 'ember-runloop';
import * as ACTION_TYPES from 'investigate-events/actions/types';

const DEFAULT_DATA = {
  data: {
    endpointId: 'id1'
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

  setEndpointId(id = 'id1') {
    this.dispatch(ACTION_TYPES.SERVICE_SELECTED, id);
    return this;
  }
}

export {
  getBrokerService,
  getBrokerServiceId,
  getConcentratorService,
  getConcentratorServiceId
};
