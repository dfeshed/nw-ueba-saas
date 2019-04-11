import reselect from 'reselect';

import { defaultMetaGroup } from 'investigate-events/reducers/investigate/dictionaries/selectors';


const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
const _meta = (state) => state.investigate.meta.meta;
const _language = (state) => state.investigate.dictionaries.language;
const _metaPanelSize = (state) => state.investigate.meta.metaPanelSize;
const _isQueryExecutedByColumnGroup = (state) => state.investigate.data.isQueryExecutedByColumnGroup;


// SELECTOR FUNCTIONS

// values object is added in init callback.
// Can safely assume a request has not been initiated for that metaKey
// if values object is not present
export const remainingMetaKeyBatches = createSelector(
  [_meta],
  (meta) => {
    return meta.filter((metaKey) => metaKey.info.isOpen && !metaKey.values);
  }
);

export const canFetchMeta = createSelector(
  [_metaPanelSize, _isQueryExecutedByColumnGroup],
  (size, isQueryExecutedByColumnGroup) => {
    return size !== 'min' && !isQueryExecutedByColumnGroup;
  }
);

export const initMetaKeyStates = createSelector(
  [_language, defaultMetaGroup],
  (language, group) => {
    return language.map((l) => {
      const key = group.keys.find((g) => g.name === l.metaName);
      if (key) {
        return {
          info: { ...l, isOpen: key.isOpen }
        };
      }
    }).filter((m) => m);
  }
);

export const isMetaStreaming = createSelector(
  [_meta],
  (meta) => {
    return meta.some((key) => key.values && key.values.status === 'streaming');
  }
);

export const emptyMetaKeys = createSelector(
  [_meta],
  (meta) => {
    return meta.filter((key) =>
      key.info.isOpen &&
      key.values &&
      key.values.complete &&
      key.values.data &&
      key.values.data.length === 0);
  }
);