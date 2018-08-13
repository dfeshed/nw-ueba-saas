import RsaContextMenu from 'rsa-context-menu/components/rsa-context-menu/component';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { getColumns } from 'investigate-events/reducers/investigate/data-selectors';
import { selectedIndex } from 'investigate-events/reducers/investigate/event-results/selectors';
import { metaFormatMap } from 'rsa-context-menu/utils/meta-format-selector';
import {
  eventsGetMore,
  eventsLogsGet,
  toggleSelectAllEvents,
  toggleEventSelection
 } from 'investigate-events/actions/events-creators';

const stateToComputed = (state) => ({
  status: state.investigate.eventResults.status,
  allEventsSelected: state.investigate.eventResults.allEventsSelected,
  selectedEventIds: state.investigate.eventResults.selectedEventIds,
  selectedIndex: selectedIndex(state),
  items: state.investigate.eventResults.data,
  aliases: state.investigate.dictionaries.aliases,
  language: state.investigate.dictionaries.language,
  columns: getColumns(state),
  endpointId: state.investigate.queryNode.serviceId,
  startTime: state.investigate.queryNode.startTime,
  endTime: state.investigate.queryNode.endTime,
  queryConditions: state.investigate.queryNode.metaFilter,
  metaFormatMap: metaFormatMap(state.investigate.dictionaries.language)
});

const dispatchToActions = {
  eventsGetMore,
  eventsLogsGet,
  toggleSelectAllEvents,
  toggleEventSelection
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

  @computed('columns', 'allItemsChecked')
  extendedColumns: (columns) => {
    return [{
      title: '',
      class: 'rsa-form-row-checkbox',
      width: 18,
      field: 'checkbox',
      dataType: 'checkbox',
      componentClass: 'rsa-form-checkbox',
      visible: true,
      disableSort: true,
      headerComponentClass: 'rsa-form-checkbox'
    }].concat(columns);
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

  actions: {
    onRowClick(event, index, browserEvent) {
      if (!browserEvent.target.className.includes('rsa-form-checkbox')) {
        this.get('selectEvent')(event);
      }
    }

  }
});

export default connect(stateToComputed, dispatchToActions)(EventsTableContextMenu);
