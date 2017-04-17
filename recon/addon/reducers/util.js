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