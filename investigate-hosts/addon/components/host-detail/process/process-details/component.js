import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import { action, computed } from '@ember/object';
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
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import { isMachineWindows } from 'investigate-hosts/reducers/details/overview/selectors';
import summaryItems from '../summary-item-config';
import { setDllRowSelectedId, toggleProcessDetailsView } from 'investigate-hosts/actions/data-creators/process';

const dispatchToActions = {
  setDllRowSelectedId,
  toggleProcessDetailsView
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
  threadList: suspiciousThreadsData(state)
});

@classic
@tagName('hbox')
@classNames('host-process-details')
class ProcessDetails extends Component {
  propertyConfig = CONFIG;
  selectedAccordion = '';

  @computed('isMachineWindows', 'selectedAccordion')
  get selectedAccordionName() {
    return this.selectedAccordion === '' && !this.isMachineWindows ? 'dll' : this.selectedAccordion;
  }

  @computed('process')
  get loadedDLLNote() {
    if (this.process.machineOsType && this.process.machineOsType !== 'linux') {
      const i18n = this.get('i18n');
      return i18n.t(`investigateHosts.process.dll.note.${this.process.machineOsType}`);
    } else {
      return '';
    }
  }

  @action
  selectAccordion(accordion) {
    this.set('selectedAccordion', accordion);
    this.send('setDllRowSelectedId', -1);
  }

  @action
  onPropertyPanelClose() {
    this.send('setDllRowSelectedId', -1);
  }

  willDestroyElement() {
    super.willDestroyElement(...arguments);
    this.send('toggleProcessDetailsView', false);
  }
}

export default connect(stateToComputed, dispatchToActions)(ProcessDetails);
