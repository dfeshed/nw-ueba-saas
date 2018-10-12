import Component from '@ember/component';
import { connect } from 'ember-redux';

const EditRankingStep = Component.extend({
  tagName: 'hbox',
  classNames: 'edit-ranking-step'
});

export default connect()(EditRankingStep);
