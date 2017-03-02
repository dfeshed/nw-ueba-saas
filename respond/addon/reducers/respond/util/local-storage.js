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
  return {
    ...initialState,
    ...(JSON.parse(localStorage.getItem(localStorageKey)) || {})
  };
};

/**
 * Mechanism to persist some of the state to local storage
 * This function will curry a given reducer (function), enabling it to persist its resulting state to a given
 * local storage key.
 * Note: this implementation may be replaced either with (a) user preference service calls, or (b) with a more
 * sophisticated solution with local storage
 * @param {function} callback A reducer that will update a given state before persisting it to local storage.
 * @param {string} localStorageKey The local storage key in which to persist state values.
 * @returns {Function} The curried reducer.
 * @public
 */
const persist = (callback, localStorageKey) => {
  return (function() {
    const state = callback(...arguments);
    const { incidentsSort, incidentsFilters, isFilterPanelOpen, isAltThemeActive } = state;
    try {
      localStorage.setItem(localStorageKey, JSON.stringify({
        incidentsFilters,
        incidentsSort,
        isFilterPanelOpen,
        isAltThemeActive
      }));
    } catch (e) {
      localStorage.setItem(localStorageKey, {});
    }
    return state;
  });
};


export {
  load,
  persist
};
