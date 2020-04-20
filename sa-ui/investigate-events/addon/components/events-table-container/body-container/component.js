import { run } from '@ember/runloop';
import { observer } from '@ember/object';
import DataTableBody from 'component-lib/components/rsa-data-table/body/component';
import {
  isLogEvent,
  eventHasLogData,
  eventLogDataIsPending
} from 'component-lib/utils/log-utils';
import { hideEventsForReQuery } from 'investigate-events/reducers/investigate/event-results/selectors';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';

/* status is being used here - https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/components/rsa-data-table/body/component.js#L57
To avoid going inside else loop , which shows noResultsMessage for a moment, which causes flickering of results */
const stateToComputed = (state) => ({
  status: state.investigate.eventResults.status,
  hideForMessaging: hideEventsForReQuery(state)
});

const EventsTableBody = DataTableBody.extend({
  insertCheckbox: true,

  // Responds to a change in the viewport by fetching log data
  // for any visible log records that need it. Debounces fetch
  // call, because scrolling may fire this handler at rapid rates.
  _visibleItemsDidChange: observer('_visibleItems', function() {
    if (this && !this.get('isDestroyed') && !this.get('isDestroying')) {
      run.debounce(this, this._fetchLogData, 1000);
    }
  }),

  @computed('_rowHeight', 'table.collapsedTuples', 'table.eventRelationshipsEnabled')
  collapsedRows(rowHeight, collapsedTuples, eventRelationshipsEnabled) {
    if (!eventRelationshipsEnabled) {
      return 0;
    } else {
      return collapsedTuples && collapsedTuples.reduce((tracker, currentValue) => {
        const { relatedEvents } = currentValue;
        return relatedEvents + tracker;
      }, 0) || 0;
    }
  },

  @computed('status', 'hideForMessaging', '_rowHeight', 'items.length', 'table.groupingSize', 'table.enableGrouping', 'table.groupLabelHeight', 'collapsedRows', 'table.eventRelationshipsEnabled')
  _minScrollHeight(status, hideForMessaging, rowHeight, itemsLength, groupSize, enableGrouping, groupLabelHeight, collapsedRows) {
    const rowsHeight = rowHeight * (itemsLength - collapsedRows);
    const groupCount = Math.floor(itemsLength / groupSize);
    if (hideForMessaging) {
      return 0;
    } else {
      if (enableGrouping) {
        if (itemsLength <= groupSize) {
          return rowsHeight || 0;
        } else if (itemsLength % groupSize === 0) {
          return rowsHeight + ((groupCount - 1) * groupLabelHeight);
        } else {
          return rowsHeight + ((groupCount) * groupLabelHeight);
        }
      } else {
        return rowsHeight || 0;
      }
    }
  },

  @computed('_firstIndex', '_rowHeight', 'clientHeight', '_distributedGroupLabelHeight', 'collapsedRows')
  _lastIndex(firstIndex, rowHeight, clientHeight, labelHeight, collapsedRows) {
    return rowHeight ? firstIndex + collapsedRows + Math.ceil(clientHeight / (rowHeight + labelHeight)) : 0;
  },

  _fetchLogData() {
    const loader = this.get('table.loadLogsAction');
    const first = this.get('items.firstObject');
    if (!first || typeof loader !== 'function') {
      return;
    }

    // Make an array of all the visible items.
    // Minor hack: rsa-data-table always renders the first item
    // (in order to measure its height), and therefore never
    // includes it in the `_visibleItems` array. So we manually
    // include it here, in case it needs log data fetched too.
    const visibles = [ first, ...this.get('_visibleItems') ];
    // Find all the visible items that need to have their log data fetched.
    const logEvents = visibles.filter((event) => {
      return isLogEvent(event) && !eventHasLogData(event) && eventLogDataIsPending(event);
    });

    if (logEvents.length) {
      loader(logEvents);
    }
  }
});

export default connect(stateToComputed)(EventsTableBody);
