import { handle } from 'redux-pack';

// ACTION_TYPES.FETCH_ENDPOINT_SERVERS
const fetchEndpointServers = (state, action) => (
  handle(state, action, {
    start: (state) => {
      return state.set('listOfEndpointServers', []);
    },
    success: (state) => {
      return state.set('listOfEndpointServers', action.payload.data);
    }
  })
);

export default {
  fetchEndpointServers
};
