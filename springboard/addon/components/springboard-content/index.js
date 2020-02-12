import Component from '@glimmer/component';
import { connect } from 'ember-redux';
import { springboardWidgets } from 'springboard/reducers/springboard/selectors';
import { widgetQuery } from 'springboard/actions/api/springboard';

const stateToComputed = (state) => ({
  springboardWidgets: springboardWidgets(state)
});

class SpringboardContentComponent extends Component {
  widgetQuery = widgetQuery
}

export default connect(stateToComputed)(SpringboardContentComponent);