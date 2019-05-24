import { combineReducers } from 'redux';
import incidentSearchParams from './add-to-incident/reducer';

export default {
  respondShared: combineReducers({
    incidentSearchParams
  })
};