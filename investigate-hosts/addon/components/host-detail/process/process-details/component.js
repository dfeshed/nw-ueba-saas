import Component from '@ember/component';
import { connect } from 'ember-redux';
import CONFIG from '../process-property-config';
import {
  getProcessData,
  isProcessLoading,
  noProcessData } from 'investigate-hosts/reducers/details/process/selectors';
import computed from 'ember-computed-decorators';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import { isMachineWindows } from 'investigate-hosts/reducers/details/overview/selectors';
import summaryItems from '../summary-item-config';

const stateToComputed = (state) => ({
  process: getProcessData(state),
  selectedDll: state.endpoint.process.selectedDllItem || {},
  summaryConfig: getColumnsConfig(state, summaryItems),
  isProcessLoading: isProcessLoading(state),
  isProcessDataEmpty: noProcessData(state),
  isMachineWindows: isMachineWindows(state)
});

const ProcessDetails = Component.extend({

  tagName: 'hbox',

  classNames: 'host-process-details',

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

export default connect(stateToComputed)(ProcessDetails);
