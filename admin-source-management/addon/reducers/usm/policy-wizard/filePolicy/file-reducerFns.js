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

// ACTION_TYPES.ADD_POLICY_FILE_SOURCE
const addPolicyFileSource = (state, action) => {
  const fields = 'policy.sources'.split('.');
  const addIndex = state.getIn(fields).length;
  fields.push(addIndex); // ['policy', 'sources', index]
  // equivalent to policy.sources[index] = payload;
  const newState = state.setIn(fields, action.payload);
  return newState;
};

// REMOVE_POLICY_FILE_SOURCE
const removePolicyFileSource = (state, action) => {
  const fields = 'policy.sources'.split('.');
  const rmIndex = action.payload;
  // const sources = state.getIn(fields).filter((e, i) => i !== rmIndex);
  const sources = state.getIn(fields).filter((e, i) => {
    return i !== rmIndex;
  });
  // equivalent to policy.sources = sources;
  const newState = state.setIn(fields, sources);
  return newState;
};

// UPDATE_POLICY_FILE_SOURCE_PROPERTY
const updatePolicyFileSourceProperty = (state, action) => {
  let newState = state;
  const fieldValuePairs = action.payload;
  for (let i = 0; i < fieldValuePairs.length; i++) {
    const { /* sourceId, */ field, value } = fieldValuePairs[i];
    const fields = field.split('.');
    // Edit the value in the policy.sources[sourceId].someField
    newState = newState.setIn(fields, value);
  }
  return newState;
};

export default {
  fetchFileSourceTypes,
  addPolicyFileSource,
  removePolicyFileSource,
  updatePolicyFileSourceProperty
};
