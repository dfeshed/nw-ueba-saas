import List from 'respond/components/rsa-list/component';
import connect from 'ember-redux/components/connect';
import { storypoints } from 'respond/selectors/storyline';

const stateToComputed = (state) => {
  return {
    items: storypoints(state)
  };
};

/**
 * @class Storyline component
 * A subclass of List component which uses `storyline/item` components to render an array of
 * storypoint objects.  @see respond/util/storypoint/ for more details.
 * @public
 */
const Storyline = List.extend({
  classNames: 'rsa-storyline',
  itemComponentClass: 'rsa-storyline/item',
  items: null
});

export default connect(stateToComputed)(Storyline);