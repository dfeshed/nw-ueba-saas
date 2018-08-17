import Component from '@ember/component';
import { isEmpty } from '@ember/utils';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import {
  RECON_DISPLAYED_HEADER,
  HAS_TOOLTIP
} from 'recon/utils/recon-event-header';
import { headerErrorMessage } from 'recon/reducers/header/selectors';
import { metaFormatMap } from 'rsa-context-menu/utils/meta-format-selector';
import HighlightsEntities from 'context/mixins/highlights-entities';
import layout from './template';
import { next } from '@ember/runloop';

const stateToComputed = ({ recon }) => ({
  headerErrorMessage: headerErrorMessage(recon),
  isHeaderOpen: recon.visuals.isHeaderOpen,
  headerItems: recon.header.headerItems,
  headerError: recon.header.headerError,
  queryInputs: recon.data.queryInputs,
  language: recon.dictionaries.language,
  metaFormatMap: metaFormatMap(recon.dictionaries.language)
});

const EventHeaderComponent = Component.extend(HighlightsEntities, {
  layout,
  tagName: 'span',
  entityEndpointId: 'CORE',
  autoHighlightEntities: true,

  @computed('headerItems')
  displayedHeaderItems(headerItems) {
    headerItems = Array.isArray(headerItems) ? headerItems : [];
    next(this, 'highlightEntities');
    return [].concat(headerItems.reduce((acc, item) => {
      // Get the sort order(so) from recon displayed header object.
      const so = RECON_DISPLAYED_HEADER[item.id];
      if (!isEmpty(item.value) && !isNaN(so) && item.name) {
        acc.push({
          name: item.name,
          value: item.value,
          type: item.type,
          key: item.key,
          so,
          hasTooltip: HAS_TOOLTIP.includes(item.name)
        });
      }
      return acc;
    }, [])).sortBy('so');
  }
});

export default connect(stateToComputed)(EventHeaderComponent);
