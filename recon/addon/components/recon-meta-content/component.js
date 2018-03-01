import Component from '@ember/component';
import { connect } from 'ember-redux';
import { isTextView } from 'recon/reducers/visuals/selectors';
import {
  eventHasPayload,
  metaHighlightCount
} from 'recon/reducers/text/selectors';
import layout from './template';

const stateToComputed = ({ recon, recon: { meta, text, data, dictionaries } }) => ({
  eventHasPayload: eventHasPayload(recon),
  isTextView: isTextView(recon),
  meta: meta.meta,
  metaError: meta.metaError,
  metaLoading: meta.metaLoading,
  metaToHighlight: text.metaToHighlight,
  metaHighlightCount: metaHighlightCount(recon),
  contextMenuItems: data.contextMenuItems,
  queryInputs: data.queryInputs,
  language: dictionaries.language
});

const MetaContentComponent = Component.extend({
  layout,
  classNameBindings: [':recon-meta-content']
});

export default connect(stateToComputed)(MetaContentComponent);
