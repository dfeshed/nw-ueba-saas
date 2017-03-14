import Ember from 'ember';
import connect from 'ember-redux/components/connect';
import { storyNodesAndLinks, storyNodesAndLinksFilter } from 'respond/selectors/storyline';

const { Component } = Ember;

const stateToComputed = (state) => ({
  data: storyNodesAndLinks(state),
  filter: storyNodesAndLinksFilter(state)
});

const IncidentEntities = Component.extend({
  // no element needed, just the child force layout
  tagName: '',
  data: null,
  filter: null,
  fitToSize: null
});

export default connect(stateToComputed)(IncidentEntities);
