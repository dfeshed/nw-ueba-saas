/**
 * Sometimes, backend parsing errors will populate the `data` field incorrectly when multiple files are involved.
 * The correct structure is that `data` should be an array of POJOs, one per each file; each POJO has a `filename` attr.
 * But due to errors, backend may mistaken create an array with just one POJO, whose `filename` is a comma-separated
 * list of file names.  So here we check for a comma-sep list and break it up.
 * Once backend fixes this issue, we won't need this UI workaround anymore.  Also, pigs may fly.
 * @see asoc-36929
 * @param {object} evt The normalized event POJO.
 * @public
 */
const normalizeEventFiles = (evt) => {
  const { data = [] } = evt;
  if (!data || !data.length) {
    return;
  }

  const extraValuesFound = [];
  data.forEach((datum) => {
    const { filename } = datum;

    // Does this filename field have multiple values comma-delimited?
    const values = filename && String(filename).split(',');
    if (values && values.length > 1) {

      // Replace it with the first delimited value, cache the others.
      // Also cache the original incorrect value, for troubleshooting.
      datum.filename_original = filename;
      datum.filename = values.shift();
      extraValuesFound.push(...values);
    }
  });

  // If we found extra values, add new entries to `data` array for each.
  extraValuesFound.forEach((value) => {
    data.push({ filename: value });
  });
};

/**
 * Sometimes, backend parsing errors will populate the `source.user.username` & `destination.user.username` fields
 * with multiple usernames, delimited by strings, especially the same username repeated over & over again.
 * The normalized event spec only supports a single value in these `username` fields, so here we workaround this issue
 * by keeping just the first comma-delimited username and stripping the others.
 * Once the backend fixes this issue, we won't need this UI workaround anymore.
 * @see asoc-36945
 * @param {object} evt The normalized event POJO.
 * @public
 */
const normalizeEventUsers = (evt) => {
  const { source, destination } = evt;
  const sourceUser = (source && source.user) ? source.user : undefined;
  const destUser = (destination && destination.user) ? destination.user : undefined;

  [ sourceUser, destUser ].forEach((user) => {
    if (user) {
      const { username } = user;

      // Does this filename field have multiple values comma-delimited?
      const values = username && String(username).split(',');
      if (values && values.length > 1) {

        // Replace it with the first delimited value.
        // Also cache the original incorrect value, for troubleshooting.
        user.username_original = username;
        user.username = values[0];
      }
    }
  });
};

/**
 * Convenience function for invoking `normalizeEvent*` functions above on an array of events.
 * @param {object[]} evts An array of normalized event POJOs.
 * @returns The same events array that was given.
 * @public
 */
const fixNormalizedEvents = (evts) => {
  if (evts) {
    evts.forEach(normalizeEventFiles);
    evts.forEach(normalizeEventUsers);
  }
  return evts;
};

export default fixNormalizedEvents;
