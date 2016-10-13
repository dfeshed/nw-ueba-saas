import Ember from 'ember';
import connect from 'ember-redux/components/connect';

import layout from './template';

const { Component } = Ember;

const stateToComputed = ({ recon: { visuals, data } }) => ({
  isHeaderOpen: visuals.isHeaderOpen,
  headerItems: data.headerItems,
  headerError: data.headerError
});

const EventHeaderComponent = Component.extend({
  layout,
  tagName: ''
});

export default connect(stateToComputed)(EventHeaderComponent);
