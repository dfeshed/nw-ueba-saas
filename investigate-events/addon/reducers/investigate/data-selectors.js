import reselect from 'reselect';
import { RECON_PANEL_SIZES } from 'investigate-events/constants/panelSizes';
import $ from 'jquery';
import { lookup } from 'ember-dependency-lookup';
import { isEmpty } from '@ember/utils';

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

export const SUMMARY_COLUMN_KEYS = {
  generic: GENERIC_SUMMARY_DATA,
  network: NETWORK_SUMMARY_DATA,
  log: LOG_SUMMARY_DATA,
  all: [ ...GENERIC_SUMMARY_DATA, ...NETWORK_SUMMARY_DATA, ...LOG_SUMMARY_DATA]
};

// ACCESSOR FUNCTIONS
const _reconSize = (state) => state.investigate.data.reconSize;
const _isReconOpen = (state) => state.investigate.data.isReconOpen;
const _metaPanelSize = (state) => state.investigate.data.metaPanelSize;
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

export const isReconFullSize = createSelector(
  [_reconSize],
  (reconSize) => reconSize === RECON_PANEL_SIZES.FULL
);

export const isDataEmpty = createSelector(
  [_data, _status],
  (data, status) => ($.isEmptyObject(data) && status === 'resolved')
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
  [getSelectedColumnGroup],
  (selectedColumns) => {
    if (selectedColumns) {
      const mutableColumns = selectedColumns.columns.asMutable();
      // slice out `custom.meta-details` column because that is a column
      // of every single meta and 1) it looks horrible and has likely
      // never been tested and 2) it causes us to keep all of the meta
      // for every event in memory and that is no beuno
      return mutableColumns.filter((col) => col.field !== 'custom.meta-details');
    }
  }
);

// returns a list of this column names involved in the creation of the events
// table. This includes flattening the `meta-summary` column.
export const getFlattenedColumnList = createSelector(
  [getColumns],
  (columns) => {
    if (columns) {
      columns = columns.map(({ field }) => field);
      // always need sessionid, also always need nwe.callback_id
      // because it determines if a row is for endpoint. Medium
      // tells us if it is log/network
      columns = [...columns, 'sessionid', 'nwe.callback_id', 'medium'];

      // If we don't have a meta-summary column we are done
      const hasMetaSummaryColumn = columns.some((field) => {
        return field === 'custom.meta-summary' || field === 'custom.metasummary';
      });
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
