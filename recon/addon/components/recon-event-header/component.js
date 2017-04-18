import Component from 'ember-component';
import { A, isEmberArray } from 'ember-array/utils';
import connect from 'ember-redux/components/connect';
import computed from 'ember-computed-decorators';
import { RECON_DISPLAYED_LOG_HEADER } from 'recon/utils/recon-event-header';
import layout from './template';
import { isLogEvent } from 'recon/reducers/meta/selectors';

const stateToComputed = ({ recon, recon: { visuals, header } }) => ({
  isHeaderOpen: visuals.isHeaderOpen,
  headerItems: header.headerItems,
  headerError: header.headerError,
  isLogEvent: isLogEvent(recon)
});

const EventHeaderComponent = Component.extend({
  layout,
  tagName: '',
  @computed('headerItems')
  displayedHeaderItems(headerItems) {
    const isLogEvent = this.get('isLogEvent');
    if (isEmberArray(headerItems) && isLogEvent) {
      const displayedItems = A([]);
      headerItems.forEach((item) => {
        // Get the (so)sort order from recon displayed header object.
        const so = RECON_DISPLAYED_LOG_HEADER[item.name];
        if (item && item.name && so) {
          // Move the item name to item id
          const id = item.name;

          // Replace the item name with a localized string
          const name = this.get('i18n').t(`recon.eventHeader.${item.name.camelize()}`);

          const { value } = item;

          // Add the properties we want to override into a new object and add to displayedItems
          displayedItems.pushObject({ id, name, so, value });
        }
      });
      // Sort the Array by the sort order parameter
      displayedItems.sort(function(a, b) {
        if (a.so > b.so) {
          return 1;
        } else if (b.so > a.so) {
          return -1;
        } else {
          return 0;
        }
      });
      return displayedItems;
    }
    return headerItems;
  }
});

export default connect(stateToComputed)(EventHeaderComponent);
