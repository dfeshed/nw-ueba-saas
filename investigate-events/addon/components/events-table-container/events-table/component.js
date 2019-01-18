import RsaContextMenu from 'rsa-context-menu/components/rsa-context-menu/component';
import computed, { and } from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';

import { getColumns } from 'investigate-events/reducers/investigate/data-selectors';
import {
  selectedIndex,
  allExpectedDataLoaded,
  areEventsStreaming,
  isCanceled
} from 'investigate-events/reducers/investigate/event-results/selectors';
import { metaFormatMap } from 'rsa-context-menu/utils/meta-format-selector';
import { eventsLogsGet } from 'investigate-events/actions/events-creators';
import {
  toggleSelectAllEvents,
  toggleEventSelection
} from 'investigate-events/actions/interaction-creators';

const stateToComputed = (state) => ({
  areEventsStreaming: areEventsStreaming(state),
  status: state.investigate.eventResults.status,
  maxEvents: state.investigate.eventResults.streamLimit,
  allEventsSelected: state.investigate.eventResults.allEventsSelected,
  selectedEventIds: state.investigate.eventResults.selectedEventIds,
  selectedIndex: selectedIndex(state),
  items: state.investigate.eventResults.data,
  aliases: state.investigate.dictionaries.aliases,
  allExpectedDataLoaded: allExpectedDataLoaded(state),
  language: state.investigate.dictionaries.language,
  columns: getColumns(state),
  endpointId: state.investigate.queryNode.serviceId,
  startTime: state.investigate.queryNode.startTime,
  endTime: state.investigate.queryNode.endTime,
  queryConditions: state.investigate.queryNode.metaFilter,
  metaFormatMap: metaFormatMap(state.investigate.dictionaries.language),
  isCanceled: isCanceled(state),
  isQueryExecutedByColumnGroup: state.investigate.data.isQueryExecutedByColumnGroup
});

const dispatchToActions = {
  eventsLogsGet,
  toggleSelectAllEvents,
  toggleEventSelection
};

// TODO bring download back
/*
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
};*/


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
  i18n: service(),

  @computed('items')
  hasResults(results) {
    return !!results && results.length > 0;
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

  @computed('columns', 'accessControl.hasInvestigateContentExportAccess', 'allItemsChecked')
  extendedColumns: (columns) => {
  // TODO bring download back
  // extendedColumns: (columns, hasPermissions) => {

    /* if (hasPermissions) {
      return [checkBoxElement, ...columns];
    }*/
    return columns;
  },

  @computed('isCanceled')
  noResultsMessage(isCanceled) {
    const i18n = this.get('i18n');
    return (isCanceled) ?
      i18n.t('investigate.empty.canceled') :
      i18n.t('investigate.empty.description');
  },

  @and('isCanceled', 'hasResults')
  hasPartialResults: false,

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

  actions: {
    onRowClick(event, index, browserEvent) {
      const notKeyboardControl = browserEvent.keyCode != 40 && browserEvent.keyCode != 38;
      const checkboxClicked = browserEvent.target.className.includes('rsa-form-checkbox-label');
      const hasCheckboxChildren = browserEvent.target.getElementsByClassName('rsa-form-checkbox-label');
      const checkboxWrapperClicked = hasCheckboxChildren && hasCheckboxChildren.length;
      if (notKeyboardControl && (checkboxClicked || checkboxWrapperClicked)) {
        this.send('toggleEventSelection', event);
      } else {
        this.get('selectEvent')(event);
      }
    }

  }
});

export default connect(stateToComputed, dispatchToActions)(EventsTableContextMenu);
