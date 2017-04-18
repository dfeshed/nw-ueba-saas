import Component from 'ember-component';
import connect from 'ember-redux/components/connect';
import { isTextView } from 'recon/selectors/type-selectors';
import layout from './template';

const stateToComputed = ({ recon, recon: { meta, text } }) => ({
  isTextView: isTextView(recon),
  meta: meta.meta,
  metaError: meta.metaError,
  metaLoading: meta.metaLoading,
  metaToHighlight: text.metaToHighlight
});

const MetaContentComponent = Component.extend({
  layout,
  classNameBindings: [':recon-meta-content', ':scroll-box']
});

export default connect(stateToComputed)(MetaContentComponent);
