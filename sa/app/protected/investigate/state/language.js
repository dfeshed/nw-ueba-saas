import Ember from 'ember';
import PromiseState from './promise-state';
import languageUtil from './helpers/language-utils';
import computed from 'ember-computed-decorators';

const { get } = Ember;

export default PromiseState.extend({
  /**
   * The default meta group for user to browse data for this Core language.
   * An object with a `keys` array. Each array item is an object with `name` & `isOpen` properties.
   * The array includes all meta keys whose flags say that they are not hidden by default.
   * @type {object}
   * @public
   */
  @computed('data')
  defaultMetaGroup(data = []) {
    let keys = data
      .reject(languageUtil.isHidden)
      .map((obj) => {
        return {
          name: get(obj, 'metaName'),
          isOpen: languageUtil.isOpen(obj)
        };
      });

    return {
      keys
    };
  }
});
