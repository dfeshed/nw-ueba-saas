import Component from '@ember/component';
import { connect } from 'ember-redux';
import { parserRuleMeta } from 'configure/reducers/content/log-parser-rules/selectors';

const stateToComputed = (state) => ({
  parserRuleMetas: parserRuleMeta(state)
});

const ValueMapping = Component.extend({
  classNames: ['value-mapping']
});
export default connect(stateToComputed)(ValueMapping);
