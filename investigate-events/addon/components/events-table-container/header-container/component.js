import $ from 'jquery';
import Component from '@ember/component';
import computed, { alias } from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import { connect } from 'ember-redux';
import { later, debounce, schedule } from '@ember/runloop';
import { RECON_PANEL_SIZES } from 'investigate-events/constants/panelSizes';
import { setColumnGroup, searchForTerm } from 'investigate-events/actions/interaction-creators';
import { getSelectedColumnGroup } from 'investigate-events/reducers/investigate/data-selectors';
import { resultCountAtThreshold } from 'investigate-events/reducers/investigate/event-count/selectors';
import { shouldStartAtOldest, actualEventCount, searchMatchesCount } from 'investigate-events/reducers/investigate/event-results/selectors';
import { thousandFormat } from 'component-lib/utils/numberFormats';
import { observer } from '@ember/object';

const stateToComputed = (state) => ({
  reconSize: state.investigate.data.reconSize,
  isReconOpen: state.investigate.data.isReconOpen,
  eventTimeSortOrder: state.investigate.data.eventAnalysisPreferences.eventTimeSortOrder,
  columnGroups: state.investigate.data.columnGroups,
  searchTerm: state.investigate.eventResults.searchTerm,
  selectedColumnGroup: getSelectedColumnGroup(state),
  count: thousandFormat(state.investigate.eventCount.data),
  isAtThreshold: resultCountAtThreshold(state),
  shouldStartAtOldest: shouldStartAtOldest(state),
  actualEventCount: thousandFormat(actualEventCount(state)),
  searchMatchesCount: searchMatchesCount(state),
  isAllEventsSelected: state.investigate.eventResults.allEventsSelected,
  selectedEventIds: state.investigate.eventResults.selectedEventIds
});

const dispatchToActions = {
  setColumnGroup,
  searchForTerm
};

const HeaderContainer = Component.extend({
  classNames: 'rsa-investigate-events-table__header',
  tagName: 'hbox',
  i18n: service(),
  accessControl: service(),
  toggleReconSize: () => {},
  toggleSlaveFullScreen: () => {},
  _searchTerm: null,

  @alias('accessControl.respondCanManageIncidents') permissionAllowsIncidentManagement: true,

  @computed('selectedEventIds', 'isAllEventsSelected')
  isIncidentButtonsDisabled(selectedEventIds, isAllEventsSelected) {
    return !((selectedEventIds && selectedEventIds.length) || isAllEventsSelected);
  },

  @computed('shouldStartAtOldest', 'i18n')
  eventResultSetStart(shouldStartAtOldest, i18n) {
    return shouldStartAtOldest ? i18n.t('investigate.events.oldest') : i18n.t('investigate.events.newest');
  },

  @computed('eventTimeSortOrder', 'i18n')
  abbreviatedSortOrder(eventTimeSortOrder, i18n) {
    return i18n.t(`investigate.events.abbr.${eventTimeSortOrder}`);
  },

  @computed('columnGroups', 'i18n.locale')
  localizedColumnGroups(columnGroups) {
    if (columnGroups) {
      return [
        { groupName: this.get('i18n').t('investigate.events.columnGroups.custom'), options: columnGroups.filter((column) => !column.ootb) },
        { groupName: this.get('i18n').t('investigate.events.columnGroups.default'), options: columnGroups.filter((column) => column.ootb) }
      ];
    }
  },

  @computed('reconSize')
  toggleEvents(size) {
    const isSizeNotMax = size !== RECON_PANEL_SIZES.MAX;
    return {
      class: isSizeNotMax ? 'shrink-diagonal-2' : 'expand-diagonal-4',
      title: isSizeNotMax ? 'investigate.events.shrink' : 'investigate.events.expand'
    };
  },

  // This is the debounced execution of the searchForTerm action creator
  // sent onKeyUp of the tethered panel input for text search
  searchForTerm() {
    this.send('searchForTerm', this._searchTerm);
  },

  didInsertElement() {
    this.set('_searchTerm', this.get('searchTerm'));
  },

  // update searchTerm when cleared via query execution
  searchTermWasChanged: observer('searchTerm', function() {
    this.set('_searchTerm', this.get('searchTerm'));
  }),

  searchPanelDidOpen() {
    schedule('afterRender', () => {
      $('.rsa-data-table-search-panel input').focus();
    });

  },

  actions: {
    debouncedSearchForTerm() {
      debounce(this, 'searchForTerm', 250);
    },

    attachTooltip() {
      later(() => {
        const customGroup = $('.ember-power-select-group-name').first();
        if (customGroup) {
          customGroup.attr('title', this.get('i18n').t('investigate.events.columnGroups.customTitle'));
        }
      }, 200);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(HeaderContainer);
