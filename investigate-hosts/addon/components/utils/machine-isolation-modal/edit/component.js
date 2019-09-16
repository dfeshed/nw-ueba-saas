import Component from '@ember/component';
import { connect } from 'ember-redux';

const stateToComputed = () => ({});

const dispatchToActions = {};

const Edit = Component.extend({
  classNames: ['edit-modal-content']
});

export default connect(stateToComputed, dispatchToActions)(Edit);