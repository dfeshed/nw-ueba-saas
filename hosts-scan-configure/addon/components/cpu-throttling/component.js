import Component from 'ember-component';
import layout from './template';
import { connect } from 'ember-redux';
import { updateScheduleProperty } from 'hosts-scan-configure/actions/data-creators';
import { cpuOptions } from 'hosts-scan-configure/reducers/schedule/selectors';

const stateToComputed = (state) => ({
  cpuOptions: cpuOptions(state)
});

const dispatchToActions = {
  updateScheduleProperty
};


const CPU_OPTIONS = Component.extend({
  layout,

  tagName: 'box',

  classNames: 'cpu-throttling'

});

export default connect(stateToComputed, dispatchToActions)(CPU_OPTIONS);
