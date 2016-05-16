/**
 * @file Initializes the theme service.
 * Injects the theme service as a global 'theme' into components.
 * @public
 */
export function initialize(application) {
  application.inject('component', 'theme', 'service:theme');
}

export default {
  name: 'theme',
  initialize
};
