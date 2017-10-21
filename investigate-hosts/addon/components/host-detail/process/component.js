import Component from 'ember-component';
import { connect } from 'ember-redux';
import SUMMARY_ITEMS from './summary-item-config';
import { getProcessData } from 'investigate-hosts/reducers/details/process/selectors';
import CONFIG from './process-property-config';
import computed from 'ember-computed-decorators';
import { toggleProcessView } from 'investigate-hosts/actions/data-creators/process';

const stateToComputed = (state) => ({
  isTreeView: state.endpoint.visuals.isTreeView,
  animation: state.endpoint.detailsInput.animation,
  process: getProcessData(state)
});

const dispatchToActions = {
  toggleProcessView
};

const Container = Component.extend({

  tagName: 'hbox',

  classNames: 'host-process-info host-process-wrapper',

  summaryConfig: SUMMARY_ITEMS,

  propertyConfig: CONFIG,

  @computed('process')
  loadedDLLNote({ machineOsType }) {
    if (machineOsType && machineOsType !== 'linux') {
      const i18n = this.get('i18n');
      return i18n.t(`investigateHosts.process.dll.note.${machineOsType}`);
    } else {
      return '';
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(Container);
