import Component from 'ember-component';
import { isEmpty } from 'ember-utils';
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
    headerItems = Array.isArray(headerItems) ? headerItems : [];
    return headerItems.reduce((acc, item) => {
      // Get the sort order(so) from recon displayed header object.
      const so = RECON_DISPLAYED_HEADER[item.id];
      if (!isEmpty(item.value) && !isNaN(so) && item.name) {
        acc.push({
          name: item.name,
          value: item.value,
          type: item.type,
          so,
          hasTooltip: HAS_TOOLTIP.includes(item.name)
        });
      }
      return acc;
    }, []).sortBy('so');
  }
});

export default connect(stateToComputed)(EventHeaderComponent);
