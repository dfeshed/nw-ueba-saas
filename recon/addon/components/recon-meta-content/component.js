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

const MetaContentComponent = Component.extend(HighlightsEntities, {
  layout,
  autoHighlightEntities: true,
  classNameBindings: [':recon-meta-content'],
  entityEndpointId: 'CORE',

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
  }
});

export default connect(stateToComputed)(MetaContentComponent);
