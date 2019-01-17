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

const dashboardView = Component.extend({
  tagName: 'grid',
  classNames: ['padding', 'max-height', 'scroll-box']
});

export default connect(stateToComputed)(dashboardView);
