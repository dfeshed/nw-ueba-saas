import { handle } from 'redux-pack';

// ACTION_TYPES.FETCH_FILE_SOURCE_TYPES
const fetchFileSourceTypes = (state, action) => (
  handle(state, action, {
    start: (state) => {
      return state.set('listOfFileSourceTypes', []);
    },
    success: (state) => {
      return state.set('listOfFileSourceTypes', action.payload.data);
    }
  })
);

export default {
  fetchFileSourceTypes
};
