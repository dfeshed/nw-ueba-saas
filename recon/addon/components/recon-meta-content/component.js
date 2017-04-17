import Component from 'ember-component';
import connect from 'ember-redux/components/connect';
import layout from './template';

const stateToComputed = ({ recon: { meta } }) => ({
  meta: meta.meta,
  metaError: meta.metaError,
  metaLoading: meta.metaLoading
});

const MetaContentComponent = Component.extend({
  layout,
  classNameBindings: [':recon-meta-content', ':scroll-box']
});

export default connect(stateToComputed)(MetaContentComponent);
