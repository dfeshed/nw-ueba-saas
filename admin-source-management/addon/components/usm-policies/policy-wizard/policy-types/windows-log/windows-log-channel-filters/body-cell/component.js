import layout from './template';
import { A } from '@ember/array';
import { isEmpty } from '@ember/utils';
import DataTableBody from '../component';
import channels from './channels';

export default DataTableBody.extend({
  layout,
  classNames: 'windows-log-channel-list',
  filterOptions: ['INCLUDE', 'EXCLUDE'],
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
    },
    // to add a new custom channel by typing it and pressing ENTER key in the channel dropdown
    setChannelOptions(select, e) {
      if (e.keyCode === 13 &&
          select.isOpen &&
          !select.highlighted &&
          !isEmpty(select.searchText)) {
        const channelOptions = this.get('channelOptions');
        const isChannelPresent = channelOptions.some((t) => (t === select.lastSearchedText));
        if (!isChannelPresent) {
          channelOptions.pushObject(select.lastSearchedText);
          select.actions.choose(select.searchText);
        }
      }
    }
  }
});