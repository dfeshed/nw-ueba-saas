import Component from '@ember/component';
import { connect } from 'ember-redux';
import CONFIG from '../process-property-config';
import {
  getProcessData,
  isProcessLoading,
  noProcessData,
  enrichedDllData,
  imageHooksData,
  suspiciousThreadsData } from 'investigate-hosts/reducers/details/process/selectors';
import computed from 'ember-computed-decorators';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import { isMachineWindows } from 'investigate-hosts/reducers/details/overview/selectors';
import summaryItems from '../summary-item-config';
import { setDllRowSelectedId } from 'investigate-hosts/actions/data-creators/process';

const dispatchToActions = {
  setDllRowSelectedId
};

const stateToComputed = (state) => ({
  process: getProcessData(state),
  selectedDll: state.endpoint.process.selectedDllItem || {},
  summaryConfig: getColumnsConfig(state, summaryItems),
  isProcessLoading: isProcessLoading(state),
  isProcessDataEmpty: noProcessData(state),
  isMachineWindows: isMachineWindows(state),
  dllList: enrichedDllData(state),
  hookList: imageHooksData(state),
  threadList: suspiciousThreadsData(state),
  isProcessDetailsView: state.endpoint.visuals.isProcessDetailsView
});

const ProcessDetails = Component.extend({

  tagName: 'hbox',

  classNames: 'host-process-details',

  propertyConfig: CONFIG,

  selectedAccordion: '',

  @computed('isMachineWindows', 'selectedAccordion')
  selectedAccordionName(isMachineWindows, selectedAccordion) {
    return selectedAccordion === '' && !isMachineWindows ? 'dll' : selectedAccordion;
  },
  @computed('process')
  loadedDLLNote({ machineOsType }) {
    if (machineOsType && machineOsType !== 'linux') {
      const i18n = this.get('i18n');
      return i18n.t(`investigateHosts.process.dll.note.${machineOsType}`);
    } else {
      return '';
    }
  },
  actions: {
    selectAccordion(accordion) {
      this.set('selectedAccordion', accordion);
      this.send('setDllRowSelectedId', -1);
    },
    onPropertyPanelClose() {
      this.send('setDllRowSelectedId', -1);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ProcessDetails);
