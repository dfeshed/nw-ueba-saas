/**
 * @file Initializes the theme service.
 * Injects the theme service as a global "theme" into components.
 */
export function initialize(application) {
    application.inject("route", "theme", "service:theme");
    application.inject("controller", "theme", "service:theme");
    application.inject("component", "theme", "service:theme");
}

export default {
  name: "theme",
  initialize
};
