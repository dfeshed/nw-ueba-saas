import Component from '@ember/component';
import { connect } from 'ember-redux';
import { isDecoder, isConcentrator } from 'direct-access/reducers/selectors';

const stateToComputed = (state) => ({
  isDecoder: isDecoder(state),
  isConcentrator: isConcentrator(state)
});

const navigationSidebar = Component.extend({
  host: window.location.host,

  tagName: 'vbox',
  classNames: ['navigation-sidebar-outer', 'border-line-right', 'border-line-top', 'flexi-fit', 'max-height']
});

export default connect(stateToComputed)(navigationSidebar);
