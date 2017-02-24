import List from 'respond/components/list/component';

/**
 * @class Storyline component
 * A subclass of List component which uses `storyline/item` components to render an array of
 * storypoint objects.  @see respond/util/storypoint/ for more details.
 * @public
 */
export default List.extend({
  classNames: 'rsa-storyline',
  itemComponentClass: 'rsa-storyline/item'
});
