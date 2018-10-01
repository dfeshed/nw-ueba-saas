import Component from '@ember/component';
import { connect } from 'ember-redux';

const DefineGroupStep = Component.extend({
  tagName: 'vbox',
  classNames: ['define-group-step', 'scroll-box']
});
export default connect()(DefineGroupStep);
