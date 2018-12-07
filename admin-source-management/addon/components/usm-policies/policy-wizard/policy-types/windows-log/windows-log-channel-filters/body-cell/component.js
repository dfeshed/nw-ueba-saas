import layout from './template';
import { A } from '@ember/array';
import DataTableBody from '../component';
import channels from './channels';

export default DataTableBody.extend({
  layout,
  classNames: 'windows-log-channel-list',
  filterOptions: ['Include', 'Exclude'],
  channelOptions: A(channels),

  actions: {
    setSelected(column, value) {
      this.set(`item.${column}`, value);
      // By default filter is Include and Event ID is ALL, if filter changes to Exclude -
      // clear out the Event ID.
      if (value.toUpperCase() === 'EXCLUDE' && column.toUpperCase() === 'FILTERTYPE') {
        this.set('item.eventId', '');
      }
      this.get('channelUpdated')();
    }
  }
});