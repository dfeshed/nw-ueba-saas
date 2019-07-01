import reselect from 'reselect';
import { lookup } from 'ember-dependency-lookup';
import { isEmpty } from '@ember/utils';
import { isEmptyObject } from 'component-lib/utils/jquery-replacement';

import { isMixedMode } from 'investigate-events/reducers/investigate/query-stats/selectors';
import { RECON_PANEL_SIZES } from 'investigate-events/constants/panelSizes';
import {
  LANGUAGE_KEY_INDEX_MASK,
  LANGUAGE_KEY_INDEX_VALUES,
  LANGUAGE_KEY_SPECIAL_SINGLETON,
  LANGUAGE_KEY_SPECIAL_MASK
} from 'investigate-events/reducers/investigate/dictionaries/utils';

const { createSelector } = reselect;

const GENERIC_SUMMARY_DATA = [
  'ip.src',
  'ipv6.src',
  'ip.dst',
  'ipv6.dst'
];

const NETWORK_SUMMARY_DATA = [
  'tcp.srcport',
  'udp.srcport',
  'tcp.dstport',
  'udp.dstport',
  'service'
];

const LOG_SUMMARY_DATA = [
  'device.type',
  'event.cat.name',
  'ec.theme'
];

const ENDPOINT_SUMMARY_DATA = [
  'category'
];

export const SUMMARY_COLUMN_KEYS = {
  generic: GENERIC_SUMMARY_DATA,
  network: NETWORK_SUMMARY_DATA,
  log: LOG_SUMMARY_DATA,
  endpoint: ENDPOINT_SUMMARY_DATA,
  all: [ ...GENERIC_SUMMARY_DATA, ...NETWORK_SUMMARY_DATA, ...LOG_SUMMARY_DATA, ...ENDPOINT_SUMMARY_DATA]
};

// ACCESSOR FUNCTIONS
const _languages = (state) => state.investigate.dictionaries.language;
const _reconSize = (state) => state.investigate.data.reconSize;
const _visibleColumns = (state) => state.investigate.eventResults.visibleColumns;
const _isReconOpen = (state) => state.investigate.data.isReconOpen;
const _metaPanelSize = (state) => state.investigate.meta.metaPanelSize;
const _data = (state) => state.investigate.eventTimeline.data;
const _status = (state) => state.investigate.eventTimeline.status;
const _columnGroups = (state) => state.investigate.data.columnGroups;
const _columnGroup = (state) => state.investigate.data.columnGroup;
export const hasColumnGroups = (state) => !isEmpty(state.investigate.data.columnGroups);
export const getDefaultPreferences = (state) => state.investigate.data.eventsPreferencesConfig.defaultPreferences.asMutable();

// SELECTOR FUNCTIONS
export const queryBodyClass = createSelector(
  [_reconSize, _isReconOpen, _metaPanelSize],
  (reconSize, isReconOpen, panelSize) => {
    let recon = 'open';
    if (!isReconOpen) {
      recon = 'closed';
    } else if (reconSize === RECON_PANEL_SIZES.MAX) {
      recon = 'expanded';
    } else if (reconSize === RECON_PANEL_SIZES.FULL) {
      recon = 'full';
    }
    return `rsa-investigate-query__body recon-is-${recon} meta-panel-size-${panelSize}`;
  }
);

export const isSummaryColumnVisible = createSelector(
  [_visibleColumns],
  (visibleColumns) => {
    return visibleColumns && visibleColumns.any((col) => col.field === 'custom.meta-summary');
  }
);

export const validEventSortColumns = createSelector(
  [_languages, isMixedMode],
  (languages, isMixedMode) => {
    if (isMixedMode || !languages) {
      return {
        columns: []
      };
    } else {
      const notIndexedAtValue = [];
      const notSingleton = [];
      const notValid = [];
      const columns = languages.filter((language) => {
        // we can only sort when indexed by value
        const isIndexedAtValue = (language.flags & LANGUAGE_KEY_INDEX_MASK) === LANGUAGE_KEY_INDEX_VALUES;

        // we can only sort singletons
        const specialBitSet = (language.flags & LANGUAGE_KEY_SPECIAL_MASK) !== 0;
        const singletonMasked = language.flags & LANGUAGE_KEY_SPECIAL_SINGLETON;
        const isSingleton = specialBitSet && (singletonMasked === LANGUAGE_KEY_SPECIAL_SINGLETON);

        // time is always sortable
        const isTime = language.format.toLowerCase().indexOf('time') > -1;

        // for any key not sortable, capture the reason
        // to be displayed as table header tooltip
        if (!isIndexedAtValue && !isSingleton) {
          notValid.push(language.metaName);
        } else if (!isSingleton) {
          notSingleton.push(language.metaName);
        } else if (!isIndexedAtValue) {
          notIndexedAtValue.push(language.metaName);
        }

        return isTime || (isIndexedAtValue && isSingleton);
      }).map((col) => col.metaName);

      return {
        columns,
        notIndexedAtValue,
        notSingleton,
        notValid
      };
    }
  }
);

export const isReconFullSize = createSelector(
  [_reconSize],
  (reconSize) => reconSize === RECON_PANEL_SIZES.FULL
);

export const isDataEmpty = createSelector(
  [_data, _status],
  (data, status) => (isEmptyObject(data) && status === 'resolved')
);

export const shouldShowStatus = createSelector(
  [isDataEmpty, _status],
  (isEmpty, status = '') => !!status.match(/wait|rejected/) || isEmpty
);

export const getCurrentPreferences = createSelector(
  [_columnGroup],
  (columnGroup) => {
    return {
      eventPreferences: { columnGroup }
    };
  }
);

export const getSelectedColumnGroup = createSelector(
  [_columnGroup, _columnGroups],
  (columnGroupId, allColumnGroups) => {
    if (allColumnGroups) {
      return allColumnGroups.find(({ id }) => id === columnGroupId) || allColumnGroups.find(({ id }) => id === 'SUMMARY');
    }
    return null;
  }
);

export const getColumns = createSelector(
  [getSelectedColumnGroup, _languages],
  (selectedColumns, languages) => {
    if (selectedColumns) {
      const mutableColumns = selectedColumns.columns.asMutable();
      // validate and prune columns against
      return mutableColumns.filter((col) => {
        // metas not included in languages are not valid
        // examples: meta blacklisted by admin based on user perms/role
        const validMeta = languages && languages.findBy('metaName', col.field);

        // summary field is special and allowed to pass
        const isSummary = col.field === 'custom.metasummary' || col.field === 'custom.meta-summary';

        // theme field is special and allowed to pass
        const isTheme = col.field === 'custom.theme';

        // slice out `custom.meta-details` column because that is a column
        // of every single meta and 1) it looks horrible and has likely
        // never been tested and 2) it causes us to keep all of the meta
        // for every event in memory and that is no beuno
        const toExclude = [
          'custom.logdata',
          'custom.source',
          'custom.destination',
          'custom.meta-details'
        ].includes(col.field);

        return (isTheme || isSummary || validMeta) && !toExclude;
      });
    }
  }
);

export const hasMetaSummaryColumn = createSelector(
  [getColumns],
  (columns = []) => {
    return columns.some((col) => {
      return col.field === 'custom.metasummary' || col.field === 'custom.meta-summary';
    });
  }
);

// returns a list of this column names involved in the creation of the events
// table. This includes flattening the `meta-summary` column.
export const getFlattenedColumnList = createSelector(
  [getColumns, hasMetaSummaryColumn],
  (columns, hasMetaSummaryColumn) => {
    if (columns) {
      columns = columns.map(({ field }) => field);
      // always need sessionid, also always need nwe.callback_id
      // because it determines if a row is for endpoint. Medium
      // tells us if it is log/network
      columns = [...columns, 'sessionid', 'nwe.callback_id', 'medium'];

      // If we don't have a meta-summary column we are done
      if (!hasMetaSummaryColumn) {
        return columns;
      }

      // Need to slice out meta-summary and then add
      // meta-summary's fields into the list
      columns = columns.filter((field) => {
        return field !== 'custom.meta-summary' && field !== 'custom.metasummary';
      });
      SUMMARY_COLUMN_KEYS.all.forEach((columnKey) => {
        const hasColumnAlready = columns.some((field) => field === columnKey);
        if (!hasColumnAlready) {
          columns.push(columnKey);
        }
      });

      return columns;
    }
  }
);

export const getColumnGroups = createSelector(
  [_columnGroups],
  (columnGroups) => {
    if (columnGroups) {
      const i18n = lookup('service:i18n');
      return [
        { groupName: i18n.t('investigate.events.columnGroups.custom'), options: columnGroups.filter((column) => !column.ootb) },
        { groupName: i18n.t('investigate.events.columnGroups.default'), options: columnGroups.filter((column) => column.ootb) }
      ];
    }
  }
);
