import Component from '@ember/component';
import { connect } from 'ember-redux';
import CONFIG from './process-property-config';
import {
  getProcessData,
  isNavigatedFromExplore,
  isProcessLoading,
  noProcessData } from 'investigate-hosts/reducers/details/process/selectors';
import computed from 'ember-computed-decorators';
import { toggleProcessView, setRowIndex } from 'investigate-hosts/actions/data-creators/process';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import { isMachineWindows } from 'investigate-hosts/reducers/details/overview/selectors';

import summaryItems from './summary-item-config';
const stateToComputed = (state) => ({
  isTreeView: state.endpoint.visuals.isTreeView,
  process: getProcessData(state),
  isNavigatedFromExplore: isNavigatedFromExplore(state),
  summaryConfig: getColumnsConfig(state, summaryItems),
  isProcessLoading: isProcessLoading(state),
  isProcessDataEmpty: noProcessData(state),
  isMachineWindows: isMachineWindows(state)
});

const dispatchToActions = {
  toggleProcessView,
  setRowIndex
};

const Container = Component.extend({

  tagName: 'box',

  classNames: ['host-process-info', 'host-process-wrapper'],

  propertyConfig: CONFIG,

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
    toggleView(closePanel) {
      closePanel();
      this.send('toggleProcessView');
    },

    onPropertyPanelClose() {
      this.send('setRowIndex', null);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(Container);
