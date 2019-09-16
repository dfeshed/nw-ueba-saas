import Component from '@ember/component';
import { connect } from 'ember-redux';

const stateToComputed = () => ({});

const dispatchToActions = {};

const Release = Component.extend({
  classNames: ['release-modal-content']
});

export default connect(stateToComputed, dispatchToActions)(Release);