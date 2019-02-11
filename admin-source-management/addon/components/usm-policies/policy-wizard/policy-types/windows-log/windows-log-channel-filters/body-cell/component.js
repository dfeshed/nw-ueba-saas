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
      const searchText = select.searchText.trim();
      const lastSearchedText = select.lastSearchedText.trim();
      // keyCode = 13 is the Enter key
      if (e.keyCode === 13 &&
          select.isOpen &&
          !select.highlighted &&
          !isEmpty(searchText)) {
        const channelOptions = this.get('channelOptions');
        const isChannelPresent = channelOptions.some((t) => (t === lastSearchedText));
        if (!isChannelPresent) {
          channelOptions.pushObject(lastSearchedText);
          select.actions.choose(searchText);
        }
      }
    }
  }
});