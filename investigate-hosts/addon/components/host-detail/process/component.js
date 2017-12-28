import Component from 'ember-component';
import { connect } from 'ember-redux';
import CONFIG from './process-property-config';
import { getProcessData, isNavigatedFromExplore } from 'investigate-hosts/reducers/details/process/selectors';
import computed from 'ember-computed-decorators';
import { toggleProcessView } from 'investigate-hosts/actions/data-creators/process';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import summaryItems from './summary-item-config';

const stateToComputed = (state) => ({
  isTreeView: state.endpoint.visuals.isTreeView,
  animation: state.endpoint.detailsInput.animation,
  process: getProcessData(state),
  isNavigatedFromExplore: isNavigatedFromExplore(state),
  summaryConfig: getColumnsConfig(state, summaryItems),
  processDetailsLoading: state.endpoint.process.processDetailsLoading
});

const dispatchToActions = {
  toggleProcessView
};

const Container = Component.extend({

  tagName: 'hbox',

  classNames: 'host-process-info host-process-wrapper',

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
