import Component from '@ember/component';
import { connect } from 'ember-redux';
import { selectedTabComponent } from 'investigate-files/reducers/visuals/selectors';

const stateToComputed = (state) => ({
  selectedTabComponent: selectedTabComponent(state)
});

const DetailComponent = Component.extend({
  tagName: 'page',
  classNames: ['rsa-investigate-files']
});

export default connect(stateToComputed)(DetailComponent);
