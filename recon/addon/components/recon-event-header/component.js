import Component from 'ember-component';
import set from 'ember-metal/set';
import { A, isEmberArray } from 'ember-array/utils';
import connect from 'ember-redux/components/connect';
import computed from 'ember-computed-decorators';
import { RECON_DISPLAYED_HEADER, HAS_TOOLTIP } from 'recon/utils/recon-event-header';
import layout from './template';

const stateToComputed = ({ recon: { visuals, header } }) => ({
  isHeaderOpen: visuals.isHeaderOpen,
  headerItems: header.headerItems,
  headerError: header.headerError
});

const EventHeaderComponent = Component.extend({
  layout,
  tagName: '',

  @computed('headerItems')
  displayedHeaderItems(headerItems) {
    if (isEmberArray(headerItems)) {
      const displayedItems = A([]);

      headerItems.forEach((item) => {
        // Get the (so)sort order from recon displayed header object.
        const so = RECON_DISPLAYED_HEADER[item.id];
        if (item && item.name && so >= 0) {
          // Add sort order to object
          item.so = so;
          set(item, 'hasTooltip', HAS_TOOLTIP.contains(item.name));
          // Add the properties we want to override into a new object and add to displayedItems
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
