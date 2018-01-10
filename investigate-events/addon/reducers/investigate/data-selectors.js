import reselect from 'reselect';
import { RECON_PANEL_SIZES } from 'investigate-events/constants/panelSizes';
import $ from 'jquery';
import { lookup } from 'ember-dependency-lookup';

const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
const _reconSize = (state) => state.investigate.data.reconSize;
const _isReconOpen = (state) => state.investigate.data.isReconOpen;
const _metaPanelSize = (state) => state.investigate.data.metaPanelSize;
const _data = (state) => state.investigate.eventTimeline.data;
const _status = (state) => state.investigate.eventTimeline.status;
const _columnGroups = (state) => state.investigate.data.columnGroups;
const _columnGroup = (state) => state.investigate.data.columnGroup;
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
      return selectedColumns.columns.asMutable();
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