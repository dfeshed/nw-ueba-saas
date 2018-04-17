/* eslint-env node */

import EmberResolver from 'ember-resolver';

/**
 * Gets the resolver class used by an Engine and creates an instance to be used
 * with test modules. Ex:
 *
 *   moduleForComponent('some-component', 'Integration Test', {
 *     resolver: engineResolverFor('ember-blog')
 *   });
 *
 * Uses the module found at `<engine-name>/resolver` as the class. If no module
 * exists at that path, then a default EmberResolver instance is created.
 *
 * You can optionally specify a modulePrefix in the event that the modulePrefix
 * differs from the engineName.
 *
 * @method engineResolverFor
 * @param {String} engineName
 * @param {String} [modulePrefix]
 * @return {Resolver}
 * @public
 */
export default function engineResolverFor(engineName, modulePrefix = engineName) {
  let Resolver;
  try {
    Resolver = require(`${engineName}/resolver`).default;
  } catch (e) {
    Resolver = EmberResolver;
  }
  return Resolver.create({ namespace: { modulePrefix } });
}
