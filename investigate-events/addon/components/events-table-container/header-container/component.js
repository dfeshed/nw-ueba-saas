import $ from 'jquery';
import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import { connect } from 'ember-redux';
import { later, debounce, schedule } from '@ember/runloop';
import { RECON_PANEL_SIZES } from 'investigate-events/constants/panelSizes';
import { setColumnGroup, searchForTerm, setSearchScroll } from 'investigate-events/actions/interaction-creators';
import { getSelectedColumnGroup } from 'investigate-events/reducers/investigate/data-selectors';
import { resultCountAtThreshold } from 'investigate-events/reducers/investigate/event-count/selectors';
import {
  actualEventCount,
  searchMatchesCount,
  eventTimeSortOrder,
  searchScrollDisplay,
  SORT_ORDER,
  areEventsStreaming
} from 'investigate-events/reducers/investigate/event-results/selectors';
import { hasMinimumCoreServicesVersionForColumnSorting } from 'investigate-events/reducers/investigate/services/selectors';
import { thousandFormat } from 'component-lib/utils/numberFormats';
import { observer } from '@ember/object';

const stateToComputed = (state) => ({
  reconSize: state.investigate.data.reconSize,
  isReconOpen: state.investigate.data.isReconOpen,
  eventTimeSortOrder: eventTimeSortOrder(state),
  columnGroups: state.investigate.data.columnGroups,
  searchTerm: state.investigate.eventResults.searchTerm,
  searchScrollDisplay: searchScrollDisplay(state),
  selectedColumnGroup: getSelectedColumnGroup(state),
  count: thousandFormat(state.investigate.eventCount.data),
  isAtThreshold: resultCountAtThreshold(state),
  actualEventCount: thousandFormat(actualEventCount(state)),
  searchMatchesCount: searchMatchesCount(state),
  selectedEventIds: state.investigate.eventResults.selectedEventIds,
  searchScrollIndex: state.investigate.eventResults.searchScrollIndex,
  sortDirection: state.investigate.data.sortDirection,
  endpointId: state.investigate.queryNode.serviceId,
  items: state.investigate.eventResults.data,
  startTime: state.investigate.queryNode.startTime,
  endTime: state.investigate.queryNode.endTime,
  areEventsStreaming: areEventsStreaming(state),
  eventTimeSortOrderPreferenceWhenQueried: state.investigate.eventResults.eventTimeSortOrderPreferenceWhenQueried,
  canSort: hasMinimumCoreServicesVersionForColumnSorting(state)
});

const dispatchToActions = {
  setColumnGroup,
  searchForTerm,
  setSearchScroll
};

const HeaderContainer = Component.extend({
  classNames: 'rsa-investigate-events-table__header',
  tagName: 'hbox',
  i18n: service(),
  accessControl: service(),
  eventBus: service(),
  toggleReconSize: () => {},
  toggleSlaveFullScreen: () => {},
  _searchTerm: null,

  /*
   * maximum events selection limit
   */
  maxEventsSelectionLimit: 1000,

  @computed('accessControl.respondCanManageIncidents', 'accessControl.investigateCanManageIncidents')
  permissionAllowsIncidentManagement(respondCanManageIncidents, investigateCanManageIncidents) {
    return investigateCanManageIncidents && respondCanManageIncidents;
  },

  @computed('hasResults', 'areEventsStreaming')
  isSearchDisabled(hasResults, areEventsStreaming) {
    return (!hasResults || areEventsStreaming);
  },

  @computed('items')
  hasResults(results) {
    return !!results && results.length > 0;
  },

  @computed('selectedEventIds')
  isIncidentButtonsDisabled(selectedEventIds) {
    if (selectedEventIds) {
      const ids = Object.keys(selectedEventIds);
      return !(ids && ids.length);
    } else {
      return true;
    }
  },

  @computed('sortDirection', 'i18n')
  eventResultSetStart(sortDirection, i18n) {
    if (sortDirection) {
      const eventResultSetStart = sortDirection.toLowerCase() === SORT_ORDER.ASC.toLowerCase() ? 'oldest' : 'newest';
      return i18n.t(`investigate.events.${eventResultSetStart}`);
    }
  },

  @computed('sortDirection', 'eventTimeSortOrderPreferenceWhenQueried', 'canSort', 'i18n')
  abbreviatedSortOrder(sortDirection, eventTimeSortOrder, canSort, i18n) {
    const sortBy = sortDirection && canSort ? sortDirection : eventTimeSortOrder;
    if (sortBy) {
      sortDirection = sortBy.toLowerCase() === SORT_ORDER.ASC.toLowerCase() ? SORT_ORDER.ASC : SORT_ORDER.DESC;
      return i18n.t(`investigate.events.abbr.${sortDirection}`);
    }
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
    this.send('searchForTerm', this._searchTerm, 0);
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
    decoratedSetColumnGroup() {
      this.get('eventBus').trigger('rsa-content-tethered-panel-hide-tableSearchPanel');
      this.send('setColumnGroup', ...arguments);
    },

    debouncedSearchForTerm(term, event) {
      if (event.key === 'Enter') {
        const { searchScrollIndex, searchMatchesCount } = this.getProperties('searchScrollIndex', 'searchMatchesCount');
        const pendingIndex = searchScrollIndex + 1 === searchMatchesCount ? 0 : searchScrollIndex + 1;
        this.send('setSearchScroll', pendingIndex);
      } else {
        debounce(this, 'searchForTerm', 500);
      }
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
