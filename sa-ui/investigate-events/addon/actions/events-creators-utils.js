/**
 * Takes a NetWitness Core event object with a `metas` array, and applies each
 * meta value as a key-value pair on the event object and then removes the
 * `metas` array.
 *
 * Example: `{metas: [ [a, b], [c, d], .. ]} => {metas: [..], a: b, c: d, ..}
 * If any duplicate keys are found in `metas`, only the last key value will be
 * applied.
 *
 * NOTE: This function will be executed thousands of times, with high frequency,
 * so its need to be performant. Therefore we forego using closures or
 * `[].forEach()` and instead use a `for` loop.
 *
 * @param {object} event
 * @public
 */
export const mergeMetaIntoEvent = (includeSessionId) => {
  return (event) => {
    if (event) {
      const { metas } = event;
      if (!metas) {
        return;
      }
      const len = metas.length || 0;
      for (let i = 0; i < len; i++) {
        const [key, val] = metas[i];
        if (event[key] === undefined) {
          event[key] = val;
        }
      }

      // convert to something easily sortable later
      // divide by 1000 as milliseconds have no meaning
      // in netwitness (for now) and no need to use up
      // the storage
      event.timeAsNumber = new Date(event.time).getTime() / 1000;

      // now that we have unraveled the metas
      // into the object remove the metas
      delete event.metas;

      if (!includeSessionId) {
        // Don't need duplicate sessionid
        delete event.sessionid;
      }
    }
  };
};