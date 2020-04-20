import { warn } from '@ember/debug';

/**
 * Load local storage values and incorporate into initial state.
 * Note: this implementation may be replaced either with (a) user preference service calls, or (b) with a more
 * sophisticated solution with local storage.
 * @param {object} initialState A set of default state values.
 * @param {string} localStorageKey The local storage key from which to load state values.
 * @returns {object} The resolve state object.
 * @public
 */
const load = (initialState, localStorageKey) => {
  let storedState;
  try {
    const json = localStorage.getItem(localStorageKey);
    storedState = json ? JSON.parse(json) : null;
  } catch (e) {
    const warnError = JSON.stringify(e);
    warn(`Unable to load state from localStorage. Applying defaults. ${localStorageKey} ${warnError}`, { id: 'respond.reducers.util.local-storage' });
  }
  return {
    ...initialState,
    ...(storedState || {})
  };
};

/**
 * Write local storage values.
 * @param {object} payload Data to be written to local storage.
 * @param {string} localStorageKey The local storage key in which to write payload.
 * @public
 */
const persist = (payload, localStorageKey) => {
  try {
    localStorage.setItem(localStorageKey, JSON.stringify(payload));
  } catch (e) {
    localStorage.setItem(localStorageKey, {});
  }
};

export {
  load,
  persist
};
