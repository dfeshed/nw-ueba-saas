import { combineReducers } from 'redux';
import incidentSearchParams from './add-to-incident/reducer';
import createIncident from './create-incident/reducers';

export default {
  respondShared: combineReducers({
    incidentSearchParams,
    createIncident
  })
};