import Component from '@ember/component';
import { scheduleOnce } from '@ember/runloop';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import _ from 'lodash';
import {
  updatePolicyProperty
} from 'admin-source-management/actions/creators/policy-wizard-creators';
import {
  fileSources,
  sourceConfig,
  fileSourcesList,
  selectedFileSource
} from 'admin-source-management/reducers/usm/policy-wizard/filePolicy/file-selectors';

const stateToComputed = (state) => ({
  sources: fileSources(state),
  columns: sourceConfig(),
  fileSourcesList: fileSourcesList(state),
  selectedFileSource: selectedFileSource(state)
});

const dispatchToActions = {
  updatePolicyProperty
};

const DefinePolicySourcesStep = Component.extend({
  tagName: 'vbox',
  classNames: ['define-policy-sources-step', 'scroll-box', 'rsa-wizard-step'],

  @computed()
  panelId() {
    return `fileTypeSourcesTooltip-${this.get('elementId')}`;
  },
  @computed('sources')
  fileSources(sources) {
    return _.cloneDeep(sources);
  },

  _updateFileSources() {
    this.send('updatePolicyProperty', 'sources', this.get('fileSources'));
  },

  _scrollToAddSelectedFileTypeBtn() {
    this.get('element').querySelector('.add-selected-file-type').scrollIntoView(false);
  },

  actions: {
    // adding a container to the sources
    addRowFilter() {
      const selectedFileSource = this.get('selectedFileSource').name;
      this.get('fileSources').pushObject({ fileType: selectedFileSource, fileEncoding: 'UTF-8', enabled: true, startOfEvents: false, sourceName: '', exclusionFilters: [] });
      this._updateFileSources();
      scheduleOnce('afterRender', this, '_scrollToAddSelectedFileTypeBtn');
    },
    // pass the index of the row to delete the row in the sources
    deleteRow(index) {
      this.get('fileSources').removeAt(index);
      this._updateFileSources();
    },
    // when the child component `body cell` modifies fileSources, this gets called
    sourceUpdated() {
      this._updateFileSources();
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(DefinePolicySourcesStep);
