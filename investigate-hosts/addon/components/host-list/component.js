import { connect } from 'ember-redux';
import Component from 'ember-component';
import {
  resetFilters
} from 'investigate-hosts/actions/data-creators/filter';

const stateToComputed = ({ endpoint: { schema } }) => ({
  schemaLoading: schema.schemaLoading
});

const dispatchToActions = {
  resetFilters
};
const Container = Component.extend({

  tagName: '',

  classNames: 'host-list show-more-filter main-zone'

});

export default connect(stateToComputed, dispatchToActions)(Container);
