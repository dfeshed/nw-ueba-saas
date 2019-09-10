import Component from '@ember/component';
import { scheduleOnce } from '@ember/runloop';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import {
  updatePolicyProperty,
  addPolicyFileSource
} from 'admin-source-management/actions/creators/policy-wizard-creators';
import {
  fileSourcesIds,
  sourceConfig,
  fileSourcesList,
  selectedFileSource,
  selectedFileSourceDefaults
} from 'admin-source-management/reducers/usm/policy-wizard/filePolicy/file-selectors';

const stateToComputed = (state) => ({
  sourcesIds: fileSourcesIds(state),
  columns: sourceConfig(),
  fileSourcesList: fileSourcesList(state),
  selectedFileSource: selectedFileSource(state),
  selectedFileSourceDefaults: selectedFileSourceDefaults(state)
});

const dispatchToActions = {
  updatePolicyProperty,
  addPolicyFileSource
};

const DefinePolicySourcesStep = Component.extend({
  tagName: 'vbox',
  classNames: ['define-policy-sources-step', 'scroll-box', 'rsa-wizard-step'],

  @computed()
  panelId() {
    return `fileTypeSourcesTooltip-${this.get('elementId')}`;
  },

  _scrollToAddSelectedFileTypeBtn() {
    this.get('element').querySelector('.add-selected-file-type').scrollIntoView(false);
  },

  actions: {
    // adding a container to the sources
    addRowFilter() {
      const selectedFileSourceDefaults = this.get('selectedFileSourceDefaults');
      this.send('addPolicyFileSource', selectedFileSourceDefaults);
      scheduleOnce('afterRender', this, '_scrollToAddSelectedFileTypeBtn');
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(DefinePolicySourcesStep);
