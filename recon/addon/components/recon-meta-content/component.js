import Component from 'ember-component';
import connect from 'ember-redux/components/connect';
import { isTextView } from 'recon/reducers/visuals/selectors';
import { eventHasPayload } from 'recon/reducers/text/selectors';
import layout from './template';

const stateToComputed = ({ recon, recon: { meta, text } }) => ({
  eventHasPayload: eventHasPayload(recon),
  isTextView: isTextView(recon),
  meta: meta.meta,
  metaError: meta.metaError,
  metaLoading: meta.metaLoading,
  metaToHighlight: text.metaToHighlight
});

const MetaContentComponent = Component.extend({
  layout,
  classNameBindings: [':recon-meta-content']
});

export default connect(stateToComputed)(MetaContentComponent);
