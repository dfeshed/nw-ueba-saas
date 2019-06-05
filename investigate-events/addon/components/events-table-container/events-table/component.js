import RsaContextMenu from 'rsa-context-menu/components/rsa-context-menu/component';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';

import { getColumns, validEventSortColumns, disableSort } from 'investigate-events/reducers/investigate/data-selectors';
import {
  areEventsStreaming,
  isCanceled,
  eventTableFormattingOpts,
  searchMatches,
  selectedIndex,
  dataCount,
  SORT_ORDER
} from 'investigate-events/reducers/investigate/event-results/selectors';
import { metaFormatMap } from 'rsa-context-menu/utils/meta-format-selector';
import { eventsLogsGet } from 'investigate-events/actions/events-creators';
import {
  toggleSelectAllEvents,
  toggleEventSelection,
  setSort
} from 'investigate-events/actions/interaction-creators';
import { setVisibleColumns } from 'investigate-events/actions/data-creators';

const stateToComputed = (state) => {
  const { columns, notIndexedAtValue, notSingleton, notValid } = validEventSortColumns(state);
  return {
    eventTableFormattingOpts: eventTableFormattingOpts(state),
    areEventsStreaming: areEventsStreaming(state),
    status: state.investigate.eventResults.status,
    searchTerm: state.investigate.eventResults.searchTerm,
    searchScrollIndex: state.investigate.eventResults.searchScrollIndex,
    allEventsSelected: state.investigate.eventResults.allEventsSelected,
    selectedEventIds: state.investigate.eventResults.selectedEventIds,
    selectedIndex: selectedIndex(state),
    items: state.investigate.eventResults.data,
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
    disableSort: disableSort(state)
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
  @computed('columns', 'accessControl.hasInvestigateContentExportAccess', 'accessControl.respondCanManageIncidents', 'allItemsChecked')
  extendedColumns: (columns, hasDownloadPermission, hasIncidentManagePermission) => {
    if (hasDownloadPermission || hasIncidentManagePermission) {
      return [checkBoxElement, ...columns];
    }
    return columns;
  },

  @computed('isCanceled')
  noResultsMessage(isCanceled) {
    const i18n = this.get('i18n');
    return (isCanceled) ?
      i18n.t('investigate.empty.canceled') :
      i18n.t('investigate.empty.description');
  },

  contextMenu({ target: { attributes } }) {
    const metaName = attributes.getNamedItem('metaname');
    const metaValue = attributes.getNamedItem('metavalue');
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
          this.send('toggleEventSelection', event);
        } else {
          this.get('eventBus').trigger('rsa-content-tethered-panel-hide-tableSearchPanel');
          this.get('selectEvent')(event);
        }
      }
    },

    toggleSort(field) {
      let sortDirection;
      if (this.get('sortField') === field && this.get('sortDirection') === SORT_ORDER.ASC) {
        sortDirection = SORT_ORDER.DESC;
      } else {
        sortDirection = SORT_ORDER.ASC;
      }

      this._toggleSort(field, sortDirection);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(EventsTableContextMenu);
