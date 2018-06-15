import Service, { inject as service } from '@ember/service';
import fetch from 'component-lib/services/fetch';
import { get } from '@ember/object';
import { log } from 'ember-debug';
import {
  mergeObjectArray,
  componentCSSList
} from 'rsa-context-menu/utils/context-menu-utils';
import { buildContextOptions } from 'rsa-context-menu/utils/build-context-options';
import _ from 'lodash';

/**
 * This service will initialize along with page boot up. This will read static json configuration file.
 * Converts static configuration to new format. Stores new format per module per meta and return corrosponding prebuild
 * context menu item on demand.
 * @public
*/
export default Service.extend({

  moduleBasedActions: null,
  i18n: service(),

  init() {
    this._super(...arguments);
    fetch('/admin/contextmenu/configuration.json')
    .then((fetched) => fetched.json())
    .then(({ data = [] }) => {
      this.set('moduleBasedActions', buildContextOptions(data, this.get('i18n')));
    }).catch((error) => {
      log('Error fetching actions', error);
    });
  },

/**
 * This public API returns context menu item for given moduleName and scope.
 * @public
*/
  getContextualActionsForGivenScope(moduleName, scope, metaFormat) {
    const actions = this.get('moduleBasedActions');
    let mergedAction = [];
    const componentCSS = (componentCSSList[moduleName] || []).concat(componentCSSList[metaFormat]);
    mergedAction = actions && actions[moduleName] && actions[moduleName][scope] ? actions[moduleName][scope] : [];
    _.forEach(componentCSS, (currentSelector) => {
      const actionsFromCss = actions && actions[moduleName] && actions[moduleName][currentSelector] ? actions[moduleName][currentSelector] : [];
      mergedAction = mergeObjectArray(mergedAction, actionsFromCss);
    });

    const i18n = get(this, 'i18n');
    return mergedAction.map((action) => {
      const { labelPrefix, labelVar } = action;
      const lookupString = `${labelPrefix}${labelVar}`;
      const hasLocale = i18n.exists(lookupString);
      const dynamicLabel = hasLocale ? i18n.t(lookupString) : labelVar;
      return _.defaults({
        label: dynamicLabel
      }, action);
    });
  }
});
