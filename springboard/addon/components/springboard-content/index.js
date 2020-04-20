import Component from '@glimmer/component';
import { connect } from 'ember-redux';
import { springboardWidgets } from 'springboard/reducers/springboard/selectors';
import { widgetQuery } from 'springboard/actions/api/springboard';
import { action } from '@ember/object';

const stateToComputed = (state) => ({
  springboardWidgets: springboardWidgets(state)
});

class SpringboardContentComponent extends Component {
  widgetQuery = widgetQuery;
  element = null;

  @action
  setup(element) {
    this.element = element;
  }

  @action
  onPagerClick(transition) {
    this.element.style = ` transition: right 0.8s; right: ${transition}px;`;
  }
}

export default connect(stateToComputed)(SpringboardContentComponent);