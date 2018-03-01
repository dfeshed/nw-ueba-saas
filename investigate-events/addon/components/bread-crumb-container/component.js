import Component from '@ember/component';
import { connect } from 'ember-redux';
import { hasRequiredValuesToQuery } from 'investigate-events/reducers/investigate/query-node/selectors';

const stateToComputed = (state) => ({
  hasRequiredValuesToQuery: hasRequiredValuesToQuery(state)
});

const BreadCrumbContainer = Component.extend({
  classNames: ['rsa-investigate-breadcrumb__crumbs', 'rsa-button-group'],
  tagName: 'nav'
});

export default connect(stateToComputed)(BreadCrumbContainer);
