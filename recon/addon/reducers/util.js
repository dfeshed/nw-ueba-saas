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

export const handlePreference = (payload, isShown) => {
  return payload && typeof isShown === 'boolean' ? isShown : true;
};