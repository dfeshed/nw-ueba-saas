/*
 * Used by text/packet reducers to add side and position
 * to each entry in their arrays
 */
export const augmentResult = (data, previousPosition = 0) => {
  return data.map((d, i) => ({
    ...d,
    side: d.side === 1 ? 'request' : 'response',
    position: previousPosition + i + 1
  }));
};

/*
 * Takes a stateVal and a payload for a toggle action
 * and determines what the state val should be.
 * Handles case where payload isn't a boolean (like with
 * action creators bound directly to ember actions)
 */
export const handleSetTo = (payload, stateVal) => {
  const hasSetTo = payload.setTo !== undefined && typeof payload.setTo === 'boolean';
  return hasSetTo ? payload.setTo : !stateVal;
};

/*
 * Takes in obj with preferences in it, and key for preference in object
 * and determines if the desired preference is present in obj, if not,
 * returns the current value for the preference. Takes care to handle
 * boolean preferences correctly.
 */
export const handlePreference = (obj, key, state) => {
  const current = state[key];

  if (!obj) {
    return current;
  }

  const value = obj[key];
  const isBoolean = typeof value === 'boolean';
  if (!isBoolean && !value) {
    return current;
  }

  return value;
};