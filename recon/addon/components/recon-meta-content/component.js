import Component from 'ember-component';
import connect from 'ember-redux/components/connect';
import layout from './template';

const stateToComputed = ({ recon: { data } }) => ({
  meta: data.meta,
  metaError: data.metaError,
  metaLoading: data.metaLoading
});

const MetaContentComponent = Component.extend({
  layout,
  classNameBindings: [':recon-meta-content', ':scroll-box']
});

export default connect(stateToComputed)(MetaContentComponent);
