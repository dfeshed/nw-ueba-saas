import Component from '@ember/component';
import { connect } from 'ember-redux';
import { serviceList } from 'investigate-hosts/reducers/hosts/selectors';

import {
  fileContextFileProperty,
  fileContextSelections,
  fileStatus,
  selectedFileChecksums
} from 'investigate-hosts/reducers/details/file-context/selectors';

import {
  setFileContextFileStatus,
  getFileContextFileStatus
} from 'investigate-hosts/actions/data-creators/file-context';


const stateToComputed = (state, { storeName }) => ({
  fileProperty: fileContextFileProperty(state, storeName),
  fileContextSelections: fileContextSelections(state, storeName),
  serviceList: serviceList(state, storeName),
  fileStatus: fileStatus(state, storeName),
  selectedFileChecksums: selectedFileChecksums(state, storeName)
});

const dispatchToActions = {
  setFileContextFileStatus,
  getFileContextFileStatus
};


const ContextWrapper = Component.extend({
  tagName: 'hbox',

  classNames: ['file-context-wrapper'],

  isPaginated: false,

  storeName: '',

  columnsConfig: null,

  propertyConfig: null,

  tabName: ''

});

export default connect(stateToComputed, dispatchToActions)(ContextWrapper);
