import Component from '@ember/component';
import { connect } from 'ember-redux';
import { serviceList } from 'investigate-hosts/reducers/hosts/selectors';

import {
  fileContextFileProperty,
  fileContextSelections,
  fileStatus,
  checksums
} from 'investigate-hosts/reducers/details/file-context/selectors';

import {
  setFileContextFileStatus,
  getFileContextFileStatus
} from 'investigate-hosts/actions/data-creators/file-context';

import computed from 'ember-computed-decorators';

const stateToComputed = (state, { storeName }) => ({
  fileProperty: fileContextFileProperty(state, storeName),
  fileContextSelections: fileContextSelections(state, storeName),
  serviceList: serviceList(state, storeName),
  fileStatus: fileStatus(state, storeName),
  checksums: checksums(state, storeName)
});

const dispatchToActions = {
  setFileContextFileStatus,
  getFileContextFileStatus
};


const ContextWrapper = Component.extend({
  classNames: ['file-context-wrapper'],

  tagName: 'hbox',

  showServiceModal: false,

  @computed('fileStatus')
  statusData(fileStatus) {
    return fileStatus ? { ...fileStatus } : {};
  }

});

export default connect(stateToComputed, dispatchToActions)(ContextWrapper);
