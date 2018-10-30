import { handle } from 'redux-pack';

// ACTION_TYPES.FETCH_LOG_SERVERS
const fetchLogServers = (state, action) => (
  handle(state, action, {
    start: (state) => {
      return state.set('listOfLogServers', []);
    },
    success: (state) => {
      return state.set('listOfLogServers', action.payload.data);
    }
  })
);

export default {
  fetchLogServers
};
