import Ember from 'ember';
import connect from 'ember-redux/components/connect';
import computed from 'ember-computed-decorators';
import { RECON_DISPLAYED_LOG_HEADER } from 'recon/utils/recon-event-header';
import layout from './template';
import { isLogEvent } from 'recon/selectors/event-type-selectors';

const {
  Component,
  isArray,
  inject: {
    service
  }
} = Ember;

const stateToComputed = ({ recon, recon: { visuals, data } }) => ({
  isHeaderOpen: visuals.isHeaderOpen,
  headerItems: data.headerItems,
  headerError: data.headerError,
  isLogEvent: isLogEvent(recon)
});

const EventHeaderComponent = Component.extend({
  layout,
  tagName: '',
  i18n: service(),
  @computed('headerItems', 'isLogEvent')
  displayedHeaderItems(headerItems, isLogEvent) {
    if (isArray(headerItems) && isLogEvent) {
      const displayedItems = [];
      headerItems.forEach((item) => {
        if (item && item.name && RECON_DISPLAYED_LOG_HEADER[item.name]) {
          // Get the (so)sort order from recon displayed header object.
          item.so = RECON_DISPLAYED_LOG_HEADER[item.name];
          // Move the item name to item id
          item.id = item.name;
          // Replace the item name with a localized string
          item.name = this.get('i18n').t(`recon.eventHeader.${item.name.camelize()}`);
          // Add the item to the displayed items.
          displayedItems.pushObject(item);
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
