import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  storyPointsWithEvents,
  storyPointSelections
} from 'respond/selectors/storyline';

const stateToComputed = (state) => ({
  items: storyPointsWithEvents(state),
  selections: storyPointSelections(state)
});

/**
 * @class Storyline component
 * A subclass of List component which uses `storyline/item` components to render an array of
 * storypoint objects.  @see respond/util/storypoint/ for more details.
 * @public
 */
const Storyline = Component.extend({
  // no element needed just the child list
  tagName: '',
  items: null,
  selections: null
});


export default connect(stateToComputed)(Storyline);