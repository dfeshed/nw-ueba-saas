import Component from 'ember-component';
import { connect } from 'ember-redux';
import { selectedTabComponent } from 'investigate-hosts/reducers/visuals/selectors';

const stateToComputed = (state) => ({
  selectedTabComponent: selectedTabComponent(state)
});

const DetailComponent = Component.extend({

  tagName: ''

});
export default connect(stateToComputed)(DetailComponent);
