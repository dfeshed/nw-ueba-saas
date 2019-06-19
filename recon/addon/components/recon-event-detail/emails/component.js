import Component from '@ember/component';
import { connect } from 'ember-redux';
import layout from './template';

const stateToComputed = () => ({
});

const dispatchToActions = {

};

const EmailReconComponent = Component.extend({
  layout
});

export default connect(stateToComputed, dispatchToActions)(EmailReconComponent);
