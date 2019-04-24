import layout from './template';
import Component from '@ember/component';
import { connect } from 'ember-redux';

const stateToComputed = ({ context: { list: { isListView } } }) => ({
  isListView
});

const AddToListComponent = Component.extend({
  layout,
  classNames: 'rsa-context-tree-table'
});

export default connect(stateToComputed)(AddToListComponent);
