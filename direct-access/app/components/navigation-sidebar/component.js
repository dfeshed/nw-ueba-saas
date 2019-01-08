import Component from '@ember/component';
import { connect } from 'ember-redux';
import { isArchiver, isBroker, isConcentrator, isDecoder, isLogDecoder } from 'direct-access/reducers/selectors';

const stateToComputed = (state) => ({
  isArchiver: isArchiver(state),
  isBroker: isBroker(state),
  isConcentrator: isConcentrator(state),
  isDecoder: isDecoder(state),
  isLogDecoder: isLogDecoder(state)
});

const navigationSidebar = Component.extend({
  host: window.location.host,

  tagName: 'vbox',
  classNames: ['navigation-sidebar-outer', 'border-line-right', 'border-line-top', 'flexi-fit', 'max-height']
});

export default connect(stateToComputed)(navigationSidebar);
