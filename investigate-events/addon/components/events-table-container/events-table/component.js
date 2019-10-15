import { observer } from '@ember/object';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';

import RsaContextMenu from 'rsa-context-menu/components/rsa-context-menu/component';
import { resultCountAtThreshold } from 'investigate-events/reducers/investigate/event-count/selectors';
import { getColumns, validEventSortColumns } from 'investigate-events/reducers/investigate/data-selectors';
import { scheduleOnce } from '@ember/runloop';
import { classicEventsURL } from 'component-lib/utils/build-url';
import {
  hasMinimumCoreServicesVersionForColumnSorting,
  summaryValuesForClassicUrl
} from 'investigate-events/reducers/investigate/services/selectors';
import {
  areEventsStreaming,
  isCanceled,
  eventTableFormattingOpts,
  searchMatches,
  selectedIndex,
  dataCount,
  areAllEventsSelected,
  nestChildEvents,
  eventsHaveSplits,
  SORT_ORDER
} from 'investigate-events/reducers/investigate/event-results/selectors';
import { metaFormatMap } from 'rsa-context-menu/utils/meta-format-selector';
import { hadTextPill, queryNodeValuesForClassicUrl } from 'investigate-events/reducers/investigate/query-node/selectors';
import { eventsLogsGet } from 'investigate-events/actions/events-creators';
import {
  toggleSelectAllEvents,
  toggleEventSelection,
  setSort
} from 'investigate-events/actions/interaction-creators';
import { setVisibleColumns } from 'investigate-events/actions/data-creators';

const stateToComputed = (state) => {
  const { columns = [], notIndexedAtValue, notSingleton, notValid } = validEventSortColumns(state);
  return {
    isReconOpen: state.investigate.data.isReconOpen,
    eventTableFormattingOpts: eventTableFormattingOpts(state),
    areEventsStreaming: areEventsStreaming(state),
    status: state.investigate.eventResults.status,
    searchTerm: state.investigate.eventResults.searchTerm,
    searchScrollIndex: state.investigate.eventResults.searchScrollIndex,
    areAllEventsSelected: areAllEventsSelected(state),
    selectedEventIds: state.investigate.eventResults.selectedEventIds,
    selectedIndex: selectedIndex(state),
    items: nestChildEvents(state),
    itemsCount: dataCount(state),
    aliases: state.investigate.dictionaries.aliases,
    language: state.investigate.dictionaries.language,
    columns: getColumns(state),
    endpointId: state.investigate.queryNode.serviceId,
    startTime: state.investigate.queryNode.startTime,
    endTime: state.investigate.queryNode.endTime,
    queryConditions: state.investigate.queryNode.metaFilter,
    metaFormatMap: metaFormatMap(state.investigate.dictionaries.language),
    isCanceled: isCanceled(state),
    threshold: state.investigate.eventCount.threshold,
    searchMatches: searchMatches(state),
    sortField: state.investigate.data.sortField,
    sortDirection: state.investigate.data.sortDirection,
    sortableColumns: columns,
    notIndexedAtValue,
    notSingleton,
    notValid,
    hasMinimumCoreServicesVersionForColumnSorting: hasMinimumCoreServicesVersionForColumnSorting(state),
    hadTextPill: hadTextPill(state),
    isAtThreshold: resultCountAtThreshold(state),
    queryNodeValuesForClassicUrl: queryNodeValuesForClassicUrl(state),
    summaryValuesForClassicUrl: summaryValuesForClassicUrl(state),
    eventRelationshipsEnabled: state.investigate.eventResults.eventRelationshipsEnabled,
    eventsHaveSplits: eventsHaveSplits(state)
  };
};

const dispatchToActions = {
  eventsLogsGet,
  toggleSelectAllEvents,
  toggleEventSelection,
  setVisibleColumns,
  setSort
};

// checkboxes for multiple event selection
const checkBoxElement = {
  title: '',
  class: 'rsa-form-row-checkbox',
  width: 18,
  field: 'checkbox',
  dataType: 'checkbox',
  componentClass: 'rsa-form-checkbox',
  visible: true,
  disableSort: true,
  headerComponentClass: 'rsa-form-checkbox'
};

/*
 * Since the events table is a special custom table which has html tags with meta and value injected from the javascript
 * we cannot use the rsa-context-menu component as-is. This extended class captures the right click event, extracts the
 * meta and value from the html span, prepares the contextSelection property before invoking the parent rsa-context-menu action.
 */
const EventsTableContextMenu = RsaContextMenu.extend({

  metaName: null,
  metaValue: null,
  selectEvent: () => {},
  accessControl: service(),
  eventBus: service(),
  i18n: service(),
  groupingSize: 100,

  // closing recon with a horizontally scrolled table results in a slow scroll to x: 0
  // this is a default browser scroll that occurs because of the element size change
  // circumvent that by manually scrolling immediately
  scrollTableLeft: observer('isReconOpen', function() {
    if (!this.get('isReconOpen')) {
      scheduleOnce('afterRender', () => {
        const table = document.querySelector('.rsa-data-table-body');
        table.scroll(0, table.scrollTop);
      });
    }
  }),

  @computed('items')
  hasResults(results) {
    return !!results && results.length > 0;
  },

  @computed('hasResults', 'areEventsStreaming')
  isSelectAllDisabled(hasResults, areEventsStreaming) {
    return (!hasResults || areEventsStreaming);
  },

  @computed('metaName', 'metaValue', 'endpointId')
  contextSelection: (metaName, metaValue) => ({ metaName, metaValue }),

  @computed('endpointId', 'startTime', 'endTime', 'queryConditions', 'language')
  contextDetails: (endpointId, startTime, endTime, queryConditions, language) => ({
    endpointId,
    startTime,
    endTime,
    queryConditions,
    language
  }),
  /*
   * Render event selection checkboxes if
   * user has permissions to either download or manage incidents
   */
  @computed('columns', 'accessControl.hasInvestigateContentExportAccess', 'accessControl.respondCanManageIncidents',
    'accessControl.investigateCanManageIncidents', 'allItemsChecked')
  extendedColumns: (columns, hasDownloadPermission, hasRespondIncidentManagePermission, hasInvestigateIncidentManagePermission) => {
    if (hasDownloadPermission || (hasRespondIncidentManagePermission && hasInvestigateIncidentManagePermission)) {
      return [checkBoxElement, ...columns];
    }
    return columns;
  },

  @computed('isCanceled', 'hadTextPill', 'queryNodeValuesForClassicUrl', 'summaryValuesForClassicUrl')
  noResultsMessage(isCanceled, hadTextPill, queryNodeValuesForClassicUrl, summaryValuesForClassicUrl) {
    const i18n = this.get('i18n');
    let message;
    if (isCanceled) {
      message = i18n.t('investigate.empty.canceled');
    } else {
      message = i18n.t('investigate.empty.description');
      if (hadTextPill) {
        const url = `${window.location.origin}/${classicEventsURL({ ...queryNodeValuesForClassicUrl, ...summaryValuesForClassicUrl })}`;
        message = i18n.t('investigate.textSearchLimitedResults', { url, message });
      }
    }
    return message;
  },

  contextMenu({ target: { attributes } }) {
    const metaName = attributes.getNamedItem('metaname');
    const metaValue = attributes.getNamedItem('metavalue');
    if (metaValue?.value) {
      // Removes aliases from metaValues. Ex: `0 [OTHER]` -> 0
      metaValue.value = metaValue.value.replace(/\[(.*?)\]/g, '').trim();
    }
    const metaFormatMap = this.get('metaFormatMap');
    if (metaName && metaValue) {
      this.setProperties({
        moduleName: 'EventAnalysisPanel',
        metaName: metaName.value,
        metaValue: metaValue.value,
        format: metaFormatMap[metaName.value]
      });
      this._super(...arguments);
    } else {
      if (this.get('contextMenuService').deactivate) {
        this.get('contextMenuService').deactivate();
      }
    } // do not call super so that the browser right-click event is preserved
  },

  _toggleSort(field, sortDirection) {
    this.send('setSort', field, sortDirection, true);
  },

  actions: {
    onRowClick(event, index, { keyCode, target }) {
      const { className } = target;
      const notKeyboardControl = keyCode != 40 && keyCode != 38;
      const checkboxClicked = className.includes('rsa-form-checkbox-label');
      const hasCheckboxChildren = target.getElementsByClassName('rsa-form-checkbox-label');
      const checkboxWrapperClicked = hasCheckboxChildren && hasCheckboxChildren.length;
      const labelClicked = className.includes('group-label') || className.includes('group-label-copy');
      if (!labelClicked) {
        if (notKeyboardControl && (checkboxClicked || checkboxWrapperClicked)) {
          this.send('toggleEventSelection', event, index);
        } else {
          this.get('eventBus').trigger('rsa-content-tethered-panel-hide-tableSearchPanel');
          this.get('selectEvent')(event);
        }
      }
    },

    toggleSort(field) {
      if (this.get('status') !== 'sorting') {
        let sortDirection;
        if (this.get('sortField') === field && this.get('sortDirection') === SORT_ORDER.ASC) {
          sortDirection = SORT_ORDER.DESC;
        } else {
          sortDirection = SORT_ORDER.ASC;
        }

        this._toggleSort(field, sortDirection);
      }
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(EventsTableContextMenu);
