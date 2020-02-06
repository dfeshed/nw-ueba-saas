import Component from '@glimmer/component';
import { connect } from 'ember-redux';
import { springboardWidgets } from 'springboard/reducers/springboard/selectors';

const stateToComputed = (state) => ({
  springboardWidgets: springboardWidgets(state)
});

class SpringboardContentComponent extends Component {
}

export default connect(stateToComputed)(SpringboardContentComponent);