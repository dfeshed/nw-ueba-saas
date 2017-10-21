import Component from 'ember-component';
import { connect } from 'ember-redux';
import { processHost } from 'investigate-hosts/reducers/details/overview/selectors';

const stateToComputed = (state) => ({
  machine: processHost(state)
});

const Summary = Component.extend({

  tagName: 'vbox',

  classNames: ['host-content']
});

export default connect(stateToComputed)(Summary);
