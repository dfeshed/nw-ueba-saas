import reselect from 'reselect';
import { isPresent } from '@ember/utils';

const { createSelector } = reselect;

const _sourcesState = (state) => state.usm.sources;
export const sources = (state) => state.usm.sources.items;
export const selectedSources = (state) => state.usm.sources.itemsSelected;
export const focusedSource = (state) => state.usm.sources.focusedItem;

export const listOfEndpoints = (state) => state.usm.sourceWizard.listOfEndpointServers || [];
export const listOfLogServers = (state) => state.usm.sourceWizard.listOfLogServers || [];

export const isSourcesLoading = createSelector(
  _sourcesState,
  (_sourcesState) => _sourcesState.itemsStatus === 'wait'
);

export const selectedEditItem = createSelector(
  selectedSources, sources,
  (items, all) => {
    // edit disabled for default windows log sources for 11.3
    if (isPresent(items) && items.length == 1 && all.findBy('id', items[0]).id !== '__default_windows_log_source') {
      const [item] = items;
      return item;
    } else {
      return 'none';
    }
  }
);

export const hasSelectedEditItem = createSelector(
  selectedEditItem,
  (item) => {
    return (isPresent(item) && (item !== 'none'));
  }
);

export const selectedDeleteItems = createSelector(
  selectedSources, sources,
  (items, all) => {
    if (items && all) {
      return items.filter((selected) => !all.findBy('id', selected).defaultSource);
    }
  }
);

export const hasSelectedDeleteItems = createSelector(
  selectedDeleteItems,
  (items) => {
    if (items) {
      return (items.length > 0);
    }
  }
);

export const selectedPublishItems = createSelector(
  selectedSources, sources,
  (items, all) => {
    if (items && all) {
      return items.filter((selected) => all.findBy('id', selected).dirty);
    }
  }
);

export const hasSelectedPublishItems = createSelector(
  selectedPublishItems,
  (items) => {
    if (items) {
      return (items.length > 0);
    }
  }
);

// ==================================
//   filters
// ==================================

// the summary list of sources objects to build the source type filter
const availableSourceSourceTypes = createSelector(
  _sourcesState,
  (_sourcesState) => {
    const sourceList = _sourcesState.items;
    const list = [];
    for (let index = 0; index < sourceList.length; index++) {
      const { sourceType } = sourceList[index];
      if (!list.includes(sourceType)) {
        list.push(sourceType);
      }
    }
    return list;
  }
);

const sourceTypeFilterConfig = createSelector(
  availableSourceSourceTypes,
  (sourceTypes) => {
    const config = {
      name: 'sourceType',
      label: 'adminUsm.sources.filter.sourceType',
      listOptions: [],
      type: 'list'
    };
    for (let i = 0; i < sourceTypes.length; i++) {
      const sourceType = sourceTypes[i];
      config.listOptions.push({
        name: sourceType,
        label: `adminUsm.sourceTypes.${sourceType}`
      });
    }
    return config;
  }
);

let sourceTypeConfigCache = null;
export const filterTypesConfig = createSelector(
  sourceTypeFilterConfig,
  (sourceTypeConfig) => {
    // only set the sourceTypeConfigCache if unset, or the first time we have list option values...
    // this avoids re-building & re-rendering every time the manage sources screen is refreshed,
    // which we don't need to do since there will always be at least one of each source type (a.k.a the default sources)
    if (!sourceTypeConfigCache || sourceTypeConfigCache.listOptions.length === 0) {
      sourceTypeConfigCache = sourceTypeConfig;
    }
    const configs = [sourceTypeConfigCache];
    return configs;
  }
);