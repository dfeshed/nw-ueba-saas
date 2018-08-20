import Component from '@ember/component';
import { connect } from 'ember-redux';
import { isTextView } from 'recon/reducers/visuals/selectors';
import HighlightsEntities from 'context/mixins/highlights-entities';
import {
  eventHasPayload,
  metaHighlightCount,
  hasTextContent,
  renderedText
} from 'recon/reducers/text/selectors';
import { isEndpointEvent } from 'recon/reducers/meta/selectors';
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
  isEndpointEvent: isEndpointEvent(recon),
  renderedText: renderedText(recon),
  hasTextContent: hasTextContent(recon),
  hasPackets: hasPackets(recon),
  meta: meta.meta,
  metaError: meta.metaError,
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

  lengthyMetas: undefined,

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

  /**
   * create array of lengthy meta keys and set lengthyMetas object with metakey and metavalue
   * as key/value pairs for endpoint events with meta containing lengthy values.
   * @private
  */
  @computed('isEndpointEvent', 'hasTextContent', 'renderedText')
  lengthyMetaKeys(isEndpointEvent, hasTextContent, renderedText) {
    const lengthyMetaKeys = [];
    const lengthyMetas = {};
    if (isEndpointEvent && hasTextContent && renderedText.length) {
      renderedText.forEach((textContent) => {
        const textData = textContent.text;

        // Split text into meta key and meta value with first occurence of '='
        const splitIndex = textData.indexOf('=');
        const metaKey = textData.substring(0, splitIndex);
        const metaValue = textData.substring(splitIndex + 1);
        lengthyMetaKeys.push(metaKey);

        // Object containing key/value pairs of metakey/metaValue
        const lengthyMeta = {};

        // replace all dots with underscores for meta key
        const translatedMetaKey = metaKey.replace(/\./g, '_');
        lengthyMeta[translatedMetaKey] = metaValue;

        Object.assign(lengthyMetas, lengthyMeta);
      });
      this.set('lengthyMetas', lengthyMetas);
    }
    return lengthyMetaKeys;
  }
});

export default connect(stateToComputed)(MetaContentComponent);
