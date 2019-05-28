import Component from '@ember/component';
import { connect } from 'ember-redux';
import { FILTER_TYPES } from './filter-types';

const stateToComputed = () => ({});

const dispatchToActions = {};

const HostDownloads = Component.extend({
  tagName: 'box',
  classNames: ['host-downloads'],

  filterTypes: FILTER_TYPES,

  actions: {
    onDeleteFilesFromServer() {
      // Placeholder
    },

    onSaveLocalCopy() {
      // placeholder
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(HostDownloads);
