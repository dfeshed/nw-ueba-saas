import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getMeta } from 'configure/reducers/logs/parser-rules/selectors';

const stateToComputed = (state) => ({
  ruleMetas: getMeta(state)
});

const ValueMapping = Component.extend({
  classNames: ['value-mapping']
});
export default connect(stateToComputed)(ValueMapping);
