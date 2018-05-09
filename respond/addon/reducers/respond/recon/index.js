import _ from 'lodash';
import { handle } from 'redux-pack';
import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import * as ACTION_TYPES from 'respond/actions/types';

const initialState = Immutable.from({
  serviceData: undefined,
  isServicesLoading: undefined,
  isServicesRetrieveError: undefined
});

export default handleActions({
  [ACTION_TYPES.SERVICES_RETRIEVE]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('isServicesLoading', true),
      failure: (s) => s.merge({ isServicesRetrieveError: true, isServicesLoading: false }),
      success: (s) => {
        const { data } = action.payload;
        const validServices = data && data.filter((service) => service !== null);
        const serviceData = _.keyBy(validServices, (service) => service.id);
        return s.merge({
          serviceData,
          isServicesLoading: false,
          isServicesRetrieveError: false
        });
      }
    });
  }

}, initialState);
