
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import Component from 'ember-component';

const stateToComputed = (state) => ({
  queryNode: state.investigate.queryNode
});

const TimeCrumb = Component.extend({
  classNames: 'rsa-investigate-breadcrumb',

  @computed()
  panelId() {
    return `breadCrumbServiceTooltip-${this.get('elementId')}`;
  }

});

export default connect(stateToComputed)(TimeCrumb);
