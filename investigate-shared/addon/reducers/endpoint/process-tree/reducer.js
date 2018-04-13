import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'investigate-shared/actions/types/endpoint';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';

const initialState = {
  rawProcessData: {
    'name': 'Evil.exe',
    'id': 1,
    'riskScore': 25,
    'children': [
      {
        'name': 'cmd.exe',
        'id': 2,
        'riskScore': 25,
        'children': [
          {
            'name': 'notepad.exe',
            'id': 3,
            'riskScore': 25,
            'children': []
          },
          {
            'name': 'winword.exe',
            'id': 4,
            'riskScore': 87
          }
        ]
      },
      {
        'name': 'cmd.exe',
        'riskScore': 15,
        'id': 6,
        'children': []
      },
      {
        'name': 'evil-new.exe',
        'riskScore': 100,
        'id': 9
      },
      {
        'name': 'cmd.exe',
        'riskScore': 25,
        'id': 8
      }
    ]
  }
};

export default reduxActions.handleActions({
  [ACTION_TYPES.FETCH_PROCESS_TREE_DATA]: (state, action) => (
    handle(state, action, {})
  )
}, Immutable.from(initialState));
