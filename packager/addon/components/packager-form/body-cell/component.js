import layout from './template';
import computed from 'ember-computed-decorators';
import { A } from 'ember-array/utils';
import { isEmpty } from 'ember-utils';
import DataTableBody from '../component';

export default DataTableBody.extend({
  layout,

  filterOptions: ['Include', 'Exclude'],

/* Localization is not in scope for the channel options presently
   since the endpoint agent doesn't support localization. Reference - PR#1991*/
  channelOptions: A(['System', 'Security', 'Application', 'Setup', 'Forwarded Events']),

  @computed('index')
  serialNumber(index) {
    return index + 1;
  },

  actions: {

    setSelected(column, value) {
      this.set(`item.${column}`, value);
    },

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
