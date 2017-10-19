import run from 'ember-runloop';
import observer from 'ember-metal/observer';
import $ from 'jquery';
import DataTableBody from 'component-lib/components/rsa-data-table/body/component';
import {
  isLogEvent,
  eventHasLogData,
  eventLogDataIsPending
} from 'component-lib/utils/log-utils';

export default DataTableBody.extend({
  // Responds to a change in the viewport by fetching log data for any visible log records that need it.
  // Debounces fetch call, because scrolling may fire this handler at rapid rates.
  _visibleItemsDidChange: observer('_visibleItems', function() {
    run.debounce(this, this._fetchLogData, 100);
  }),

  _fetchLogData() {
    const loader = this.get('table.loadLogsAction');
    const first = this.get('items.firstObject');
    if (!first || !$.isFunction(loader)) {
      return;
    }

    // Make an array of all the visible items.
    // Minor hack: rsa-data-table always renders the first item (in order to measure its height), and therefore never
    // includes it in the `_visibleItems` array. So we manually include it here, in case it needs log data fetched too.
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
