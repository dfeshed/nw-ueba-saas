import Component from '@ember/component';
import { connect } from 'ember-redux';
import { isTextView } from 'recon/reducers/visuals/selectors';
import HighlightsEntities from 'context/mixins/highlights-entities';
import {
  eventHasPayload,
  metaHighlightCount,
  hasTextContent
} from 'recon/reducers/text/selectors';
import {
  errorMessage
} from 'recon/reducers/meta/selectors';
import {
  hasPackets
} from 'recon/reducers/packets/selectors';
import layout from './template';
import computed from 'ember-computed-decorators';
import { next } from '@ember/runloop';
import { metaFormatMap } from 'rsa-context-menu/utils/meta-format-selector';
import { highlightMeta } from 'recon/actions/interaction-creators';

const stateToComputed = ({ recon, recon: { meta, text, data, dictionaries } }) => ({
  eventHasPayload: eventHasPayload(recon),
  isTextView: isTextView(recon),
  hasTextContent: hasTextContent(recon),
  hasPackets: hasPackets(recon),
  meta: meta.meta,
  metaError: errorMessage(recon),
  metaLoading: meta.metaLoading,
  metaToHighlight: text.metaToHighlight,
  metaHighlightCount: metaHighlightCount(recon),
  queryInputs: data.queryInputs,
  language: dictionaries.language,
  metaFormatMap: metaFormatMap(dictionaries.language)
});

const dispatchToActions = {
  highlightMeta
};

const MetaContentComponent = Component.extend(HighlightsEntities, {
  layout,
  autoHighlightEntities: true,
  classNameBindings: [':recon-meta-content'],
  entityEndpointId: 'CORE',

  groupByOptions: ['none', 'alphabet'],

  _selectedGroupBy: null,

  /**
   * This computed is call to ensure highlightEntities is being called after all meta loaded.
   * @private
  */
  @computed('meta', 'hasTextContent', 'hasPackets')
  metaItems(meta, hasTextContent, hasPackets) {
    if (hasTextContent || hasPackets || meta) {
      next(this, 'highlightEntities');
    }
    return meta;
  },

  @computed('groupByOptions', '_selectedGroupBy')
  selectedGroupBy: {
    get(groupByOptions, _selectedGroupBy) {
      return _selectedGroupBy || groupByOptions[0];
    },

    set(value) {
      this.set('_selectedGroupBy', value);
      return value;
    }
  },

  actions: {
    changeOption(option) {
      this.set('selectedGroupBy', option);

      // Reset highlightMeta when meta grouping drop down option changes
      this.send('highlightMeta', null);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(MetaContentComponent);
