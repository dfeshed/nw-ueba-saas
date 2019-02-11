import layout from './template';
import { A } from '@ember/array';
import { isEmpty } from '@ember/utils';
import DataTableBody from '../component';
import channels from './channels';
import { next } from '@ember/runloop';

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
    // When onBlur happens due to ENTER/TAB press or the CLICK event,
    // whatever channel name that has been entered will be added to the dropdown list.
    setChannelOptionsOnBlur(select) {
      const searchText = select.searchText.trim();
      const lastSearchedText = select.lastSearchedText.trim();
      if (!select.isOpen &&
          !select.highlighted &&
          !isEmpty(searchText)) {
        const channelOptions = this.get('channelOptions');
        const isChannelPresent = channelOptions.some((t) => (t === lastSearchedText));
        if (!isChannelPresent) {
          next(this, () => {
            // push the lastSearchedText in the next runloop so EPS has time to
            // react to the ENTER/TAB press or the CLICK event.
            channelOptions.pushObject(lastSearchedText);
            select.actions.choose(searchText);
          });
        }
      }
    }
  }
});