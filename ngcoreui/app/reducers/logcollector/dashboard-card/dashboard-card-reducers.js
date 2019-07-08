import Immutable from 'seamless-immutable';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import * as ACTION_TYPES from 'ngcoreui/actions/types';

const initialState = {
  protocols: '',
  items: [],
  itemsStatus: 'wait'
};

export default reduxActions.handleActions({

  [ACTION_TYPES.LOG_COLLECTOR_FETCH_PROTOCOLS]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        return state.merge({
          items: [],
          itemsStatus: 'wait'
        });
      },
      failure: (state) => {
        return state.set('itemsStatus', 'error');
      },
      success: (state) => {
        let arr = null;
        if (action.payload != null && action.payload.nodes != null) {
          arr = action.payload.nodes.map((x) => ({ protocol: x.name }));
        }
        return state.merge({
          items: arr,
          itemsStatus: 'complete'
        });
      }
    })
  )

}, Immutable.from(initialState));
