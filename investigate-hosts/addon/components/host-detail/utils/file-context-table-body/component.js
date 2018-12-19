import { run } from '@ember/runloop';
import { observer } from '@ember/object';
import DataTableBody from 'component-lib/components/rsa-data-table/body/component';
import { connect } from 'ember-redux';
import { fetchMachineCount } from 'investigate-hosts/actions/data-creators/file-context';

const dispatchToActions = {
  fetchMachineCount
};
const FilesContextTableBody = DataTableBody.extend({
  insertCheckbox: true,

  // Responds to a change in the viewport by fetching log data for any visible log records that need it.
  // Debounces fetch call, because scrolling may fire this handler at rapid rates.
  _visibleItemsDidChange: observer('_visibleItems', function() {
    run.debounce(this, this._fetchMachineCount, 100);
  }),

  _fetchMachineCount() {
    if (this.get('isDestroyed') || this.get('isDestroying')) {
      return;
    }
    const first = this.get('items.firstObject');
    const agentCountMapping = this.get('table.agentCountMapping');
    if (!first) {
      return;
    }

    // Make an array of all the visible items.
    // Minor hack: rsa-data-table always renders the first item (in order to measure its height), and therefore never
    // includes it in the `_visibleItems` array. So we manually include it here, in case it needs log data fetched too.
    const visibles = [ first, ...this.get('_visibleItems') ].mapBy('checksumSha256');
    // Find all the visible items that need to have their log data fetched.
    const items = visibles.filter((item) => {
      return agentCountMapping && !agentCountMapping.hasOwnProperty(item);
    });

    if (items.length) {
      this.send('fetchMachineCount', items, this.get('table.tabName'));
    }
  }
});

export default connect(null, dispatchToActions)(FilesContextTableBody);
