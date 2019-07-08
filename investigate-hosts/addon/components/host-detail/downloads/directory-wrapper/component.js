import Component from '@ember/component';
import { connect } from 'ember-redux';

const stateToComputed = (state) => ({
  mftDirectory: state.endpoint.hostDownloads.mftDirectory.subDirectories
});

const DirectoryWrapper = Component.extend({
  tagName: 'section',
  classNames: ['directory-wrapper']
});

export default connect(stateToComputed)(DirectoryWrapper);